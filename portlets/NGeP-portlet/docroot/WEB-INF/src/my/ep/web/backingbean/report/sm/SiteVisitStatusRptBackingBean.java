package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SiteVisitStatusRptManagedBean;

@ManagedBean (name="siteVisitStatusRptBB")
@ViewScoped
public class SiteVisitStatusRptBackingBean extends SmReportBackingBean<SiteVisitStatusRptManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(SiteVisitStatusRptBackingBean.class);

	public SiteVisitStatusRptBackingBean(){
		onChangeZone();
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		checkOneYearDifferent(getManagedBean().getDateFrom(), getManagedBean().getDateTo());
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		params.put(SmReportConst.PARAM_DISTRICT, getManagedBean().getDistrict());
		params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean().getApplStatus());
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setZone(null);
		getManagedBean().setApplNo(null);
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
		getManagedBean().setApplStatus(null);
	}
	
	public void onChangeZone() {
		initStatesByZone();
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
	}
	
	public void onChangeState(){
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			Long stateId = Long.parseLong(getManagedBean().getState());
			getManagedBean().setDistrictLst(retrieveDistrictLsByStateId(stateId));
		}else{
			getManagedBean().setDistrictLst(null);
			getManagedBean().setDistrict(null);
		}
	}
}
