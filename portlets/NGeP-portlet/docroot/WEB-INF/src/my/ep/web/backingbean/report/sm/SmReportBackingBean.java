package my.ep.web.backingbean.report.sm;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SmReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class SmReportBackingBean<T extends SmReportManagedBean>
		extends ReportBaseBackingBean<T> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SmReportBackingBean.class);

	public void generateSmReport(LinkedHashMap<String, String> params) {
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		params.put(SmReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(SmReportConst.PARAM_REPORT_CODE, getManagedBean()
				.getReportCode());
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean()
				.getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateFrom() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateFrom(fromDate.getTime());
			getManagedBean().setDateTo(new Date());
		}
	}

	public void onChangeZone() {

	}

	public void onChangeRegType() {

	}

	public void reportOnChange() {
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}

	public void addCompanyNameParam(LinkedHashMap<String, String> params){
		if(getManagedBean().getSelectedOrgProfile() != null){
			if(getManagedBean().getSelectedOrgProfile().getSmSupplier() != null){
				params.put(SmReportConst.PARAM_COMPANY_NAME, 
						getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			}
		}
	}
}
