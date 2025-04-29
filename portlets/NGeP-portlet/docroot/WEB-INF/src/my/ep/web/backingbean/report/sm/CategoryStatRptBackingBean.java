package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.CategoryStatRptManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name="categoryStatRptBB")
@ViewScoped
public class CategoryStatRptBackingBean extends SmReportBackingBean<CategoryStatRptManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(CategoryStatRptBackingBean.class);
	
	public CategoryStatRptBackingBean() {
		// #7754_#35984 - Start
		initStates();
		getZoneByUser();
		initStatesByZone();
		// #7754_#35954 - End
		getManagedBean().setRegStatusLs(retrieveRegStatusList());
		//#32521 - START
		setDefaultDateFromAndDateTo();
		//#32521 - END
	}
	
	// #7754_#35984 - Start
	@Override
	public void initStatesByZone() {
		getManagedBean().setDistrictLst(new ArrayList<SelectItem>());
		if (StringUtils.isBlank(getManagedBean().getZone())) {
			initStatesForCatStat();
		} else {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			reqDTO.setZoneName(getManagedBean().getZone());
			List<PmState> stateList = SmServiceLookup.getStateSO()
					.retriveStatesListByCountryCodeWithZoneName(reqDTO)
					.getStateList();
			getManagedBean().setStateLstByZone(
					EntityHelper.convertListToSelectItem(stateList,
							"stateName", "stateId"));
		}
	}

	/**
	 * If there is no zone, it will input all states without by zone for report
	 * SM_002
	 */
	public void initStatesForCatStat() {
		PmCountry country = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		getManagedBean().setStateLstByZone(
				SmCommonUtil.getStateList(country.getCountryId()));
	}

	/**
	 * To generate district list based from state id by zone for report SM_002
	 */
	public void onChangeStateByZone() {
		if (StringUtils.isNotBlank(getManagedBean().getStateByZone())) {
			Long stateId = Long.parseLong(getManagedBean().getStateByZone());
			getManagedBean().setDistrictLst(
					retrieveDistrictLsByStateId(stateId));
		} else {
			getManagedBean().setDistrictLst(null);
		}
	}

	private void addStateParamByZone(LinkedHashMap<String, String> params) {
		if (StringUtils.isNotBlank(getManagedBean().getStateByZone())) {
			params.put(SmReportConst.PARAM_STATE, getManagedBean()
					.getStateByZone());
		}
	}

	// #7754_#35984 - End
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		if(getManagedBean().getReportCode().equalsIgnoreCase(SmReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)){
			
			//#7754_#35984 - Start
			addStateParamByZone(params);
			//#7754_#35984 - End
			addDistrictParam(params);
			
			//#32521 - START
			SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
			
			if(StringUtils.isNotBlank(getManagedBean().getDisciplinaryActionStatus())){
				params.put(SmReportConst.PARAM_DA_STATUS, getManagedBean().getDisciplinaryActionStatus());
			}
			//#32521 - END
			
			if(StringUtils.isNotBlank(getManagedBean().getSupplierType())){
				params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
			}
			
			if(getManagedBean().getRegStatus() != null){
				params.put(SmReportConst.PARAM_REG_STATUS, getManagedBean().getRegStatus().toString());
			}
			
			addCategoryParam(params);
		}else if(getManagedBean().getReportCode().equalsIgnoreCase(SmReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL)){
			addStateParam(params);
			addDistrictParam(params);
			addCategoryParam(params);
		}

		//log.info(Arrays.asList(params)); 
		
		generateSmReport(params);
	}
	
	private void addStateParam(LinkedHashMap<String, String> params){
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}		
	}
	
	private void addDistrictParam(LinkedHashMap<String, String> params){
		if(StringUtils.isNotBlank(getManagedBean().getDistrict())){
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean().getDistrict());
		}	
	}
	
	public void resetSearch(){
		getManagedBean().setSupplierType(null);
		getManagedBean().setRegStatus(null);
		getManagedBean().setState(null);
		getManagedBean().setDistrict(null);
		resetCategory();
		//#32521 - START
		setDefaultDateFromAndDateTo();
		getManagedBean().setDisciplinaryActionStatus(null);
		//#32521 - END
		// #7754_#35984 - Start
		getManagedBean().setStateByZone(null);
		// #7754_#35984 - End
	}

	public void onChangeState(){
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			Long stateId = Long.parseLong(getManagedBean().getState());
			getManagedBean().setDistrictLst(retrieveDistrictLsByStateId(stateId));
		}else{
			getManagedBean().setDistrictLst(null);
		}
	}
}