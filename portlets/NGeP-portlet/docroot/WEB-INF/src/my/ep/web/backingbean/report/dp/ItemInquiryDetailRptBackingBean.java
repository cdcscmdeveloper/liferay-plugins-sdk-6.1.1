package my.ep.web.backingbean.report.dp;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.managedbean.report.dp.ItemInquiryDetailRptManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "itemInquiryDetailRptBackingBean")
@ViewScoped
public class ItemInquiryDetailRptBackingBean extends DpReportBackingBean<ItemInquiryDetailRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ItemInquiryDetailRptBackingBean.class);
	
	public void generateReport(LinkedHashMap<String, String> params){
		generateDpReport(params);
	}
	
	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc() throws NamingException {
		return super.getStatusDropdownValues(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setStatus(null);
		getManagedBean().setGroupBy(null);
		getManagedBean().setDocNo(null);
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		log.info("Date From>>>>>>" + getManagedBean().getDateFrom());
		log.info("Date To>>>>>>" + getManagedBean().getDateTo());
		log.info("Group>>>>>>" + getManagedBean().getScPIDtlsPrcRptGroup());
		log.info("Status>>>>>>" + getManagedBean().getStatus());
		log.info("Doc No>>>>>>" + getManagedBean().getDocNo());
		
		retrieveReportValueFromDb();
		if (getManagedBean().getDateFrom() != null) {
			params.put(DpReportConst.PARAM_PI_FROM_DATE, df.format(getManagedBean().getDateFrom()));
		}
		if (getManagedBean().getDateTo() != null) {
			params.put(DpReportConst.PARAM_PI_TO_DATE, df.format(getManagedBean().getDateTo()));
		}
		if (getManagedBean().getStatus() != null) {
			params.put(DpReportConst.PARAM_STATUS, getManagedBean().getStatus().toString());
		}
		if (getManagedBean().getScPIDtlsPrcRptGroup() != null) {
			params.put(DpReportConst.PARAM_GROUP, getManagedBean().getScPIDtlsPrcRptGroup());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getDocNo())) {
			params.put(DpReportConst.PARAM_PI_NO, getManagedBean().getDocNo());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			if(getManagedBean().getSelectedOrgProfile().getSmSupplier() != null){
				log.info("Org Profile>>>>>>" + getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
				params.put(DpReportConst.PARAM_SUPPLIER, 
						getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			}
		}
		return params;
	}

	
	/**
	 * On supplier change
	 */
	public void onSupplierChange() {
	}
}
