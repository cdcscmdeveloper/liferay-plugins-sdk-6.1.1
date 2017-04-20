package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.UserSoftcertReqRptManagedBean;

@ManagedBean (name="userSoftcertReqRptBB")
@ViewScoped
public class UserSoftcertReqRptBackingBean extends SmReportBackingBean<UserSoftcertReqRptManagedBean>{
	private static final long serialVersionUID = 1L;
	
	public UserSoftcertReqRptBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		
		if(StringUtils.isNotBlank(getManagedBean().getUserName())){
			params.put(SmReportConst.PARAM_USER_NAME, getManagedBean().getUserName());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getMofNo())){
			params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getRegNo())){
			params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getIdentificationNo())){
			params.put(SmReportConst.PARAM_IDENT_NO, getManagedBean().getIdentificationNo());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getSoftcertDateType())){
			params.put(SmReportConst.PARAM_DATE_TYPE,getManagedBean().getSoftcertDateType());
			
			if(getManagedBean().getDateFrom() != null){
				params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
				params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
			}
		}
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setUserName(null);
		getManagedBean().setMofNo(null);
		getManagedBean().setRegNo(null);
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setSoftcertDateType(null);
		getManagedBean().setIdentificationNo(null);
	}
}
