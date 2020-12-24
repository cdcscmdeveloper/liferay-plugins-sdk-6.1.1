package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constants.CommonConstants;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SvoSiteVisitLocRptManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "svoSiteVisitLocRptBB")
@ViewScoped
public class SvoSiteVisitLocRptBackingBean extends
		SmReportBackingBean<SvoSiteVisitLocRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6486404832435176253L;

	public SvoSiteVisitLocRptBackingBean() {
		onChangeZone();
		setDefaultDateFromAndDateTo();
		initSvoUser();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		checkOneYearDifferent(getManagedBean().getDateFrom(), getManagedBean()
				.getDateTo());

		addCompanyNameParam(params);
		if (!StringUtils.isBlank(getManagedBean().getZone())) {
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}
		if (!StringUtils.isBlank(getManagedBean().getSvoUser())) {
			params.put(SmReportConst.PARAM_SVO_USER, getManagedBean()
					.getSvoUser());
		}

		if (!StringUtils.isBlank(getManagedBean().getApplNo())) {
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean()
					.getApplNo());
		}

		if (!StringUtils.isBlank(getManagedBean().getState())) {
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}

		if (!StringUtils.isBlank(getManagedBean().getDistrict())) {
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean()
					.getDistrict());
		}

		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));

		//12625 start - add param
		if (!StringUtils.isBlank(getManagedBean().getSvoApplicationStatus())) {
		params.put(SmReportConst.PARAM_SVO_APPL_STATUS, getManagedBean()
				.getSvoApplicationStatus());
		}
		if (!StringUtils.isBlank(getManagedBean().getSvoPerformSiteVisit())) {
		params.put(SmReportConst.PARAM_SVO_PERFORM_SITE_VISIT, getManagedBean()
				.getSvoPerformSiteVisit());
		}
		//12625 end
		
//		addCategoryParam(params);

		generateSmReport(params);
	}

	public void resetSearch() {
		setDefaultDateFromAndDateTo();
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setZone(null);
		getManagedBean().setApplNo(null);
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
		getManagedBean().setSvoUser(StringUtils.EMPTY);
		resetCategory();
	}

	public void onChangeState() {
		if (StringUtils.isNotBlank(getManagedBean().getState())) {
			Long stateId = Long.parseLong(getManagedBean().getState());
			getManagedBean().setDistrictLst(
					retrieveDistrictLsByStateId(stateId));
		} else {
			getManagedBean().setDistrictLst(null);
		}
	}

	public void onChangeZone() {
		initStatesByZone();
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
	}

	private void initSvoUser() {
		getManagedBean().setSvoUserList(new ArrayList<SelectItem>());
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO
					.retrieveUPSSVOUser(new FacesScopes().getUserSessionBean()
							.getUserId());
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (Object[] object : ptLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					getManagedBean().getSvoUserList().add(selectItem);
				}
			}
		}
	}

}
