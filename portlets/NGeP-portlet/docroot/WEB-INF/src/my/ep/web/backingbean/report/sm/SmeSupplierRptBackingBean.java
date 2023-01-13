package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.SmeSupplierRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smeSupplierRptBB")
@ViewScoped
public class SmeSupplierRptBackingBean extends SmReportBackingBean<SmeSupplierRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(SmeSupplierRptBackingBean.class);

	public SmeSupplierRptBackingBean() {
		buildStatusTypeList();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		if (StringUtils.isNotBlank(getManagedBean().getAccountType())) {
			params.put(SmReportConst.PARAM_ACCOUNT_TYPE, getManagedBean().getAccountType());
			// #29617 ReOpen - Start
			if(this.getManagedBean().getAccountType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
				if (getManagedBean().getZone() != null) {
					params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
				}
				if (getManagedBean().getSupplierType() != null) {
					params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
				}
			}
			// #29617 ReOpen - End
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.getCompanyName());
		}
		// #29617 ReOpen - remove code
		if (getManagedBean().getSmeStatusType() != null) {
			params.put(SmReportConst.PARAM_SME_STATUS_TYPE, getManagedBean().getSmeStatusType());
		}
		if (getManagedBean().getSmeCorpRegistered() != null) {
			params.put(SmReportConst.PARAM_SME_CORP_REG, getManagedBean().getSmeCorpRegistered());
		}
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		if (getManagedBean().getDateStartFrom() != null) {
			getManagedBean().setDateFrom(getManagedBean().getDateStartFrom());
			getManagedBean().setDateTo(getManagedBean().getDateStartTo());
			params.put(SmReportConst.PARAM_START_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_START_DATE_TO, df.format(getManagedBean().getDateTo()));
		} else if (getManagedBean().getDateEndFrom() != null) {
			getManagedBean().setDateFrom(getManagedBean().getDateEndFrom());
			getManagedBean().setDateTo(getManagedBean().getDateEndTo());
			params.put(SmReportConst.PARAM_END_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_END_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setAccountType(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		getManagedBean().setSupplierType(null);
		getManagedBean().setSmeStatusType(null);
		getManagedBean().setSmeCorpRegistered(null);
		getManagedBean().setDateStartFrom(null);
		getManagedBean().setDateStartTo(null);
		getManagedBean().setDateEndFrom(null);
		getManagedBean().setDateEndTo(null);
	}

	public void setSmeDateToByDateRange(int StartEnd, int dateRange) {
		if (StartEnd == 1) {
			getManagedBean().setDateFrom(getManagedBean().getDateStartFrom());
		} else if (StartEnd == 2) {
			getManagedBean().setDateFrom(getManagedBean().getDateEndFrom());
		}
		Calendar calTo = Calendar.getInstance();
		Date dtFrom = getManagedBean().getDateFrom();
		calTo.setTime(dtFrom);
		calTo.add(Calendar.MONTH, dateRange);
		getManagedBean().setDateTo(calTo.getTime());
	}

	private void buildStatusTypeList() {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		reqDTO.setParameterType(ParameterTypeConst.SME_STATUS_TYPE);
		reqDTO.setLanguageCode(languageCode);
		ParameterResDTO resDTO = SmServiceLookup.getParameterSO().retrieveParameterDescListByTypeAndParamCodes(reqDTO);
		List<SelectItem> statusType = new ArrayList<SelectItem>();
		// #29617 Reopen Start - note #119 item 2
		while(statusType.size() < 6) statusType.add(null);
		for (PmParameterDesc list : resDTO.getParameterDescList()) { 
			SelectItem type = new SelectItem(); 
			type.setValue(list.getPmParameter().getParameterCode()); 
			type.setLabel(list.getCodeDesc()); 
			if(type.getValue().toString().equalsIgnoreCase("MANMICRO")){
				statusType.set(0, type);
			}else if(type.getValue().toString().equalsIgnoreCase("MANSMALL")){
				statusType.set(1, type);
			}else if(type.getValue().toString().equalsIgnoreCase("MANMED")){
				statusType.set(2, type);
			}else if(type.getValue().toString().equalsIgnoreCase("SERMICRO")){
				statusType.set(3, type);
			}else if(type.getValue().toString().equalsIgnoreCase("SERSMALL")){
				statusType.set(4, type);
			}else if(type.getValue().toString().equalsIgnoreCase("SERMED")){
				statusType.set(5, type);
			}
		//#29617 Reopen End 
		}
		getManagedBean().setStatusTypeList(statusType);
	}
}
