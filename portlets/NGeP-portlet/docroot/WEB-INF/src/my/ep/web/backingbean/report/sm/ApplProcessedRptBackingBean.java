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
import my.ep.web.managedbean.report.sm.ApplProcessedRptManagedBean;

@ManagedBean(name = "applProcessedRptBB")
@ViewScoped
public class ApplProcessedRptBackingBean extends SmReportBackingBean<ApplProcessedRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ApplProcessedRptBackingBean.class);
	
	public ApplProcessedRptBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_SUPPLIER_ID,
					String.valueOf(getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId()));
		}
		
		if(getManagedBean().getSupplierType()  != null){
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
		
		if(getManagedBean().getApplStatus() != null){
			params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean().getApplStatus());
		}
		
		if (getManagedBean().getApplType() != null) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
					.getApplType());
		}
		
		//#32521 - reopen - START
		if(getManagedBean().getSelectedUser() != null){
			params.put(SmReportConst.PARAM_USER_ID2, String.valueOf(getManagedBean().getSelectedUser().getUserId()));
		}
		//#32521 - reopen - END
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		//#38227 - START
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setApplStatus(StringUtils.EMPTY);
		//#38227 - END
		setDefaultDateFromAndDateTo();
		getManagedBean().setSupplierType(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		getManagedBean().setSelectedUser(null);
		getManagedBean().setApplType(null);
		getManagedBean().setApplStatusList(null);
	}
}