package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplRequireSiteVisitRptManagedBean;

@ManagedBean (name="applRequireSiteVisitRptBB")
@ViewScoped
public class ApplRequireSiteVisitRptBackingBean extends SmReportBackingBean<ApplRequireSiteVisitRptManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(ApplRequireSiteVisitRptBackingBean.class);
	
	public ApplRequireSiteVisitRptBackingBean(){
		onChangeZone();
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		checkOneYearDifferent(getManagedBean().getDateFrom(), getManagedBean().getDateTo());
		
		addCompanyNameParam(params);
		if(StringUtils.isNotBlank(getManagedBean().getZone())){
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getApplNo())){
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getDistrict())){
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean().getDistrict());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getSiteVisitStatus())){
			params.put(SmReportConst.PARAM_SV_STATUS, getManagedBean().getSiteVisitStatus());
		}
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		
		addCategoryParam(params);
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		setDefaultDateFromAndDateTo();
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		getManagedBean().setApplNo(null);
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
		getManagedBean().setSiteVisitStatus(null);
		resetCategory();
	}
	
	public void onChangeState(){
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			Long stateId = Long.parseLong(getManagedBean().getState());
			getManagedBean().setDistrictLst(retrieveDistrictLsByStateId(stateId));
		}else{
			getManagedBean().setDistrictLst(null);
		}
	}
	
	public void onChangeZone() {
		initStatesByZone();
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
	}
}
