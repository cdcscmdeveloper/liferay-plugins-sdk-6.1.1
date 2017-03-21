package my.ep.web.backingbean.rpt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.type.pm.OrganizationType;
import my.ep.util.pm.OrgProfileUtil;

@ManagedBean(name = "expenditurePlanDetailReportBackingBean")
@SessionScoped
public class ExpenditurePlanDetailReportBackingBean extends RptBackingBean{

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

	public ExpenditurePlanDetailReportBackingBean(){
		
		super("PM_004", "Expenditure Plan Detail Report", false, false);
		this.lovValuesMap = new HashMap<String, String>();
		
	}
	
	public void reset(){
		
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
		
	}
	
	public void generateReport(){
		
		HashMap<String, String> parameter = new HashMap<String, String>();
	
		parameter.put("report_id", super.getReportId());
		parameter.put("report_name", super.getReportName());
		parameter.put("user_id", String.valueOf(super.getUserId()));
		
		if(selectedMinistry != null){
			parameter.put("ministry_id", String.valueOf(selectedMinistry.getOrgProfileId()));
		}
		
		if(selectedPegwaiPengawal != null){
			parameter.put("pp_id", String.valueOf(selectedPegwaiPengawal.getOrgProfileId()));
		}
		
		if(selectedKumpulanPTJ != null){
			parameter.put("kptj_id", String.valueOf(selectedKumpulanPTJ.getOrgProfileId()));
		}
		
		if(selectedPTJ != null){
			parameter.put("ptj_id", String.valueOf(selectedPTJ.getOrgProfileId()));
		}
		
		parameter.put("year", ppYear);
		
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
		
		if (isEmpty(lovValuesMap)){
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
			
		if(!isEmpty(selectedPTJ) && !isEmpty(selectedPTJ.getPmOrgValidity())){
			lovValuesMap.put("3", selectedPTJ.getPmOrgValidity().getOrgName());
		}
	}
	
	protected boolean isEmpty(List<?> list){
		return list == null || list.isEmpty();
	}
	
	protected boolean isEmpty(HashMap map){
		return map == null || map.isEmpty();
	}
	
	protected boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}
	
	protected boolean isEmpty(Object obj){
		return obj == null;
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