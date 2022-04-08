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

import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SmeSupplierRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smeSupplierRptBB")
@ViewScoped
public class SmeSupplierRptBackingBean extends SmReportBackingBean<SmeSupplierRptManagedBean> {
	private static final long serialVersionUID = 1L;

	public SmeSupplierRptBackingBean() {
		buildStatusTypeList();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		if (StringUtils.isNotBlank(getManagedBean().getAccountType())) {
			params.put(SmReportConst.PARAM_ACCOUNT_TYPE, getManagedBean().getAccountType());
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.getCompanyName());
		}
		if (getManagedBean().getZone() != null) {
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}
		if (getManagedBean().getSupplierType() != null) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
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
		for (PmParameterDesc list : resDTO.getParameterDescList()) {
			SelectItem type = new SelectItem();
			type.setValue(list.getPmParameter().getParameterCode());
			type.setLabel(list.getCodeDesc());
			statusType.add(type);
		}
		getManagedBean().setStatusTypeList(statusType);
	}
}