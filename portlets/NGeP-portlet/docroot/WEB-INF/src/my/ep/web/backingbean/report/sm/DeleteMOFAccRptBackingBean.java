package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DeleteMOFAccRptManagedBean;

@ManagedBean(name="deleteMOFAccRptBB")
@ViewScoped
public class DeleteMOFAccRptBackingBean extends SmReportBackingBean<DeleteMOFAccRptManagedBean>{
	private static final long serialVersionUID = 1L;

	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

		if(StringUtils.isNotBlank(getManagedBean().getZone())){
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}else{
			params.put(SmReportConst.PARAM_ZONE, StringUtils.EMPTY);
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getSupplierType())){
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getCancelBy())){
			params.put(SmReportConst.PARAM_CANCEL_BY, getManagedBean().getCancelBy());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getApplStatus())){
			params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean().getApplStatus());
		}
		
		if(getManagedBean().getDateFrom() != null){
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setZone(null);
		getManagedBean().setSupplierType(null);
		getManagedBean().setCancelBy(null);
		getManagedBean().setApplStatus(null);
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);

	}
}
