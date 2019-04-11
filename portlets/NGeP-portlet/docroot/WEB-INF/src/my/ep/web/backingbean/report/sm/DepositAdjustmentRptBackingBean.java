package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DepositAdjustmentRptManagedBean;

@ManagedBean(name = "depositAdjustmentRptBB")
@ViewScoped
public class DepositAdjustmentRptBackingBean extends SmReportBackingBean<DepositAdjustmentRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(DepositAdjustmentRptBackingBean.class);

	public DepositAdjustmentRptBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		if(getManagedBean().getSelectedOrgProfile() != null){
			if(getManagedBean().getSelectedOrgProfile().getSmSupplier() != null){
				params.put(SmReportConst.PARAM_COMPANY_NAME, 
						getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			}
		}
		
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		setDefaultDateFromAndDateTo();
		getManagedBean().setSelectedOrgProfile(null);
	}
}
