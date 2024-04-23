package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ProcAndRegPaymentRptManagedBean;

@ManagedBean(name = "procAndRegPaymentRptBB")
@ViewScoped
public class ProcAndRegPaymentRptBackingBean extends SmReportBackingBean<ProcAndRegPaymentRptManagedBean>{
	private static final long serialVersionUID = 1L;

	public ProcAndRegPaymentRptBackingBean(){
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			setDefaultDateFromAndDateTo();
		}
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

		
		addCompanyNameParam(params);
		
		if(StringUtils.isNotBlank(getManagedBean().getPaymentType())){
			params.put(SmReportConst.PARAM_PAYMENT_TYPE, getManagedBean().getPaymentType());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getApplType())){
			params.put(SmReportConst.PARAM_APPL_TYPE,getManagedBean().getApplType());
		}

		if(StringUtils.isNotBlank(getManagedBean().getDateFrom().toString())){
			checkOneYearDifferent(getManagedBean().getDateFrom(),getManagedBean().getDateTo());
			
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		
		//#7754 - Start
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		//#7754 - End

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setApplType(null);
		getManagedBean().setPaymentType(null);
		setDefaultDateFromAndDateTo();
	}
}
