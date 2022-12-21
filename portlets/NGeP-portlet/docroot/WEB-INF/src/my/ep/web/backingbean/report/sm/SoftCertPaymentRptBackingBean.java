package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SoftCertPaymentRptManagedBean;

@ManagedBean(name = "softCertPaymentRptBB")
@ViewScoped
public class SoftCertPaymentRptBackingBean extends SmReportBackingBean<SoftCertPaymentRptManagedBean>{
	private static final long serialVersionUID = 1L;

	public SoftCertPaymentRptBackingBean(){
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			setDefaultDateFromAndDateTo();
		}
	}
	
	public void generateReport(){
		checkOneYearDifferent(getManagedBean().getDateFrom(),getManagedBean().getDateTo());
		
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedOrgProfile(null);
		
		setDefaultDateFromAndDateTo();
	}
}
