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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

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
		getManagedBean().setCurrentActiveOrg(null);
		getManagedBean().setSelectedMinistry(null);
		getManagedBean().setSelectedPegawaiPengawal(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
		getManagedBean().setSelectedYear(null);
		getManagedBean().setDisabledMinistryLov(false);
		getManagedBean().setDisabledPegawaiLov(false);
		getManagedBean().setDisabledKumpulanPTJLov(false);
		getManagedBean().setDisabledPTJLov(false);
		getManagedBean().setProcurementCatId(null);
		getManagedBean().setProcurementTypeId(null);
		getManagedBean().setStatus(null);
		
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
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
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
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
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
			//[#24976] - start
			if(PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL.equals(this.getManagedBean().getReportCode())) {
				range = 8;
			}
			//[#24976] - end
			for (int i = year - range; i <= year; i++) {
				yearList.add(Integer.toString(i));
			}
		}		
		//[#24976] - start
		if(PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL.equals(this.getManagedBean().getReportCode())) {
			this.getManagedBean().setSelectedYear(yearFrom);
		}
		//[#24976] - end
		return yearList;
	}

	public void generatePpReport() {
		IRPTRequestSO rptRequestSO = null;
		
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
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
