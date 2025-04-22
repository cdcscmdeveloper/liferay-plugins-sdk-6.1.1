package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplApprovedRptManagedBean;

@ManagedBean(name = "applApprovedRptBB")
@ViewScoped
public class ApplApprovedRptBackingBean extends SmReportBackingBean<ApplApprovedRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ApplApprovedRptBackingBean.class);
	
	public ApplApprovedRptBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		if(getManagedBean().getSelectedOrgProfile() != null){
			if(getManagedBean().getSelectedOrgProfile().getSmSupplier() != null){
				params.put(SmReportConst.PARAM_COMPANY_NAME, 
						getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			}
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getMofNo().toString())){
			params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		}
		
		//#32521 - START
		if (!StringUtils.isBlank(getManagedBean().getApplType())) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
					.getApplType());
		}
		//#32521 - reopen - START
		if(getManagedBean().getSelectedUser() != null){
			params.put(SmReportConst.PARAM_USER_ID2, String.valueOf(getManagedBean().getSelectedUser().getUserId()));
		}
		//#32521 - reopen - END
		//#32521 - END
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		setDefaultDateFromAndDateTo();
		getManagedBean().setMofNo(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		//#32521 - START
		getManagedBean().setSelectedUser(null);
		getManagedBean().setApplType(null);
		//#32521 - END
	}
}
