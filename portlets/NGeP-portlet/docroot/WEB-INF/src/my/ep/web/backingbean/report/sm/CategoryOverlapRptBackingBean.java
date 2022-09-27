package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CategoryOverlapRptManagedBean;

@ManagedBean(name = "categoryOverlapRptBB")
@ViewScoped
public class CategoryOverlapRptBackingBean extends
		SmReportBackingBean<CategoryOverlapRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3668612835203377029L;
	
	public CategoryOverlapRptBackingBean(){
		addCategory();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		if (StringUtils.isNotBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
					.getSupplierType());
		}
		
		//#7754 - Start
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		//#7754 - End

		addCategoryParam(params);

		generateSmReport(params);
	}
	
	public void resetSearch() {
		getManagedBean().setSupplierType(StringUtils.EMPTY);
		getManagedBean().setCategoryLs(null);
		addCategory();
	}

}
