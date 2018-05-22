package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CategoryStatRptManagedBean;

@ManagedBean(name="categoryStatRptBB")
@ViewScoped
public class CategoryStatRptBackingBean extends SmReportBackingBean<CategoryStatRptManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(CategoryStatRptBackingBean.class);
	
	public CategoryStatRptBackingBean(){
		initStates();
		getManagedBean().setRegStatusLs(retrieveRegStatusList());
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		if(getManagedBean().getReportCode().equalsIgnoreCase(SmReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)){
			
			addStateParam(params);
			addDistrictParam(params);
			
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