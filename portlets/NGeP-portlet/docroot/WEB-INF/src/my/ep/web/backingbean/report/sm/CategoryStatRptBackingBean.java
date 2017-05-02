package my.ep.web.backingbean.report.sm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CategoryStatRptManagedBean;
import my.ep.web.vo.CategoryRptVO;

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
		
		if(StringUtils.isNotBlank(getManagedBean().getState())){
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}		
		
		if(StringUtils.isNotBlank(getManagedBean().getDistrict())){
			params.put(SmReportConst.PARAM_DISTRICT, getManagedBean().getDistrict());
		}	
		
		if(StringUtils.isNotBlank(getManagedBean().getSupplierType())){
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
		
		if(getManagedBean().getRegStatus() != null){
			params.put(SmReportConst.PARAM_REG_STATUS, getManagedBean().getRegStatus().toString());
		}
		
		addCategoryParam(params);

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
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