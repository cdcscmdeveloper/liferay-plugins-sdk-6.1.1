package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CompSuppTypeRegStatusRptManagedBean;

@ManagedBean(name = "compSuppTypeRegStatusRptBB")
@ViewScoped
public class CompSuppTypeRegStatusRptBackingBean extends
		SmReportBackingBean<CompSuppTypeRegStatusRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5361772840939984239L;

	public CompSuppTypeRegStatusRptBackingBean() {
		addCategory();
		initBusinesTypeList();
		onChangeBussType();
		getManagedBean().setRegStatusLs(retrieveRegStatusList());
		onChangeZone();  //#33783
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		if (StringUtils.isNotBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
					.getSupplierType());
		}
		if (!StringUtils.isBlank(getManagedBean().getBussType())) {
			params.put(SmReportConst.PARAM_BUSS_TYPE, getManagedBean()
					.getBussType());
		}
		if (getManagedBean().getRegStatus() != null) {
			params.put(SmReportConst.PARAM_REG_STATUS, getManagedBean()
					.getRegStatus().toString());
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		//#33783 - start
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}
		if(StringUtils.isNotBlank(getManagedBean().getDistrict())){
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean().getDistrict());
		}
		if(getManagedBean().getIsCLBG() != null){
			params.put(SmReportConst.PARAM_IS_CLBG, getManagedBean().getIsCLBG() ? "1" : "0");
		}
		/*
		if (!StringUtils.isBlank(getManagedBean().getMofRegNoFrom())) {
			params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
					.getMofRegNoFrom());
		}
		if (!StringUtils.isBlank(getManagedBean().getMofRegNoTo())) {
			params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
					.getMofRegNoTo());
		}
		*/
		//#33783 - end

		addCatParamWithoutMainCatChecking(params);

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType(StringUtils.EMPTY);
		getManagedBean().setCategoryLs(null);
		getManagedBean().setBussType(StringUtils.EMPTY);
		getManagedBean().setRegStatus(null);
		getManagedBean().setMofRegNoFrom(StringUtils.EMPTY);
		getManagedBean().setMofRegNoTo(StringUtils.EMPTY);
		//#33783 - start
		getManagedBean().setIsCLBG(null);
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
		//#33783 - end
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		addCategory();
	}
	
	//#33783 - start
	public void onChangeZone() {
		initStatesByZone();
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
	}
	
	public void onChangeState(){
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			Long stateId = Long.parseLong(getManagedBean().getState());
			getManagedBean().setDistrictLst(retrieveDistrictLsByStateId(stateId));
		}else{
			getManagedBean().setDistrictLst(null);
		}
	}
	//#33783 - end

}
