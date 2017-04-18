package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.web.managedbean.report.sm.CategoryStatRptManagedBean;

@ManagedBean(name="categoryStatRptBB")
@ViewScoped
public class CategoryStatRptBackingBean extends SmReportBackingBean<CategoryStatRptManagedBean>{
	private static final long serialVersionUID = 1L;
	
	public CategoryStatRptBackingBean(){
		initStates();
		getManagedBean().setRegStatusLs(retrieveRegStatusList());
	}
	
	public void generateReport(){
		checkTwoYearsFieldDefference(getManagedBean().getYear(),getManagedBean().getYearTo());
		
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

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