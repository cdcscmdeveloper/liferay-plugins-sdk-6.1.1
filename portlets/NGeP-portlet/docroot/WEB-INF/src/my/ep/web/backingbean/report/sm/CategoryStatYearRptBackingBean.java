package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CategoryStatYearRptManagedBean;

@ManagedBean(name="categoryStatYearRptBB")
@ViewScoped
public class CategoryStatYearRptBackingBean extends SmReportBackingBean<CategoryStatYearRptManagedBean>{
	private static final long serialVersionUID = 1L;
	
	public CategoryStatYearRptBackingBean(){
		getManagedBean().setYearList(getCommonYearList());
	}
	
	public void generateReport(){
		checkTwoYearsFieldDefference(getManagedBean().getYear(),getManagedBean().getYearTo());
		
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_YEAR_FROM, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_YEAR_TO, getManagedBean().getYearTo());

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
	}

}
