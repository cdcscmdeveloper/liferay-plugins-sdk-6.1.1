package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ReqDelApplWithStatRptManagedBean;
import my.ep.web.managedbean.report.sm.UserSoftcertReqRptManagedBean;

@ManagedBean (name="reqDelApplWithStatRptBB")
@ViewScoped
public class ReqDelApplWithStatRptBackingBean extends SmReportBackingBean<ReqDelApplWithStatRptManagedBean>{
	private static final long serialVersionUID = 1L;
	
	public ReqDelApplWithStatRptBackingBean(){
		getManagedBean().setProcessingOfficerLs(retrieveProcessingOfficerList());
		setDefaultDateFromAndDateTo();
		
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		
		if(StringUtils.isNotBlank(getManagedBean().getSelectedPO())){
			params.put(SmReportConst.PARAM_PROC_OFFICER, getManagedBean().getSelectedPO());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getZone())){
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}else{
			params.put(SmReportConst.PARAM_ZONE, StringUtils.EMPTY);
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getSupplierType())){
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getApplNo())){
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		}
		
		if(getManagedBean().getDateFrom() != null){
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		getManagedBean().setSupplierType(null);
		
		getManagedBean().setApplNo(null);
		getManagedBean().setReportType(null);
		getManagedBean().setSelectedPO(null);
		
//		getManagedBean().setDateFrom(null);
//		getManagedBean().setDateTo(null);
		setDefaultDateFromAndDateTo();

	}
}
