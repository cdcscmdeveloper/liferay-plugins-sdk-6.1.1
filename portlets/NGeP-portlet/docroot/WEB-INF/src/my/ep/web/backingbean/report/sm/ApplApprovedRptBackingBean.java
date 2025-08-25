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
		LinkedHashMap<String,String> params = new LinkedHashMap<>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		//#32521 - START
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(
					SmReportConst.PARAM_SUPPLIER_ID,
					String.valueOf(getManagedBean().getSelectedOrgProfile()
							.getSmSupplier().getSupplierId()));
		}
		//#32521 - END
		if(StringUtils.isNotBlank(getManagedBean().getMofNo())){
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
		if (!StringUtils.isBlank(getManagedBean().getSupplierType())){
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
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
		//#38227 - START
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		setDefaultDateFromAndDateTo();
		//#38227 - END
		//#32521 - START
		getManagedBean().setSelectedUser(null);
		getManagedBean().setApplType(null);
		getManagedBean().setSupplierType(null);
		//#32521 - END
	}
}
