package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SupplierStatRptManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "supplierStatRptBB")
@ViewScoped
public class SupplierStatRptBackingBean extends
		SmReportBackingBean<SupplierStatRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 935908261846525960L;

	public SupplierStatRptBackingBean() {
		onChangeZone();
		setDefaultDateFromAndDateTo();
	}

	public void onChangeZone() {
		initStatesByZone();
	}

	public void onChangeState() {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(Long.parseLong(getManagedBean().getState()));
		List<PmDistrict> districtList = SmServiceLookup.getDistrictSO()
				.retriveDistrictListByStateID(reqDTO).getDistrictList();
		getManagedBean().setDistrictLst(
				EntityHelper.convertListToSelectItem(districtList,
						"districtName", "districtId"));
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		if (!StringUtils.isBlank(getManagedBean().getState())) {
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}
		if (!StringUtils.isBlank(getManagedBean().getDistrict())) {
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean()
					.getDistrict());
		}

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setState(StringUtils.EMPTY);
		getManagedBean().setDistrict(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
