package my.ep.web.backingbean.report.fl.pa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.backingbean.report.fl.po.ItemThresholdRptBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.pa.ItemPanelThresholdRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.StatusHelper;

/**
 * Backing Bean for Item Panel Threshold
 * 
 * @author luqmanhakim
 * @since 13/10/2025
 *
 */
@ManagedBean(name = "itemPanelThrRptBB")
@SessionScoped
public class ItemPanelThresholdRptBackingBean extends FlReportBackingBean<ItemPanelThresholdRptManagedBean> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = EpLogger.getLogger(ItemThresholdRptBackingBean.class);
	
	public ItemPanelThresholdRptBackingBean() {
		if(getManagedBean().getMinistry() == null){
			defaultPtjAndMinistryValues();
		}
	}
	
	public SelectItem[] getDropdownValuesProcurementType() {
		SelectItem[] items = null;
		String parameterType = FlReportConst.FL_PARAMETER_TYPE_RPT;
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType, null);
			
	        List<SelectItem> itemList = new ArrayList<SelectItem>();
	        itemList.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
	            ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
	
	        if (parameterDescs != null) {
	            for (PmParameterDesc parameterDesc : parameterDescs) {
	                if (parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
	                    itemList.add(new SelectItem(parameterDesc.getPmParameter().getParameterCode(),
	                        parameterDesc.getCodeName()));
	                }
	            }
	        }
	        items = itemList.toArray(new SelectItem[itemList.size()]);
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}
	
	public static List<SelectItem> getFinancialYearOpt() {
		List<SelectItem> financialYearOpt = new ArrayList<SelectItem>();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    
	    int currentYear = cal.get(Calendar.YEAR);
	    int startYear = 2018;

	    for (int year = currentYear ; year >= startYear ; year--) {
	        financialYearOpt.add(new SelectItem(year, Integer.toString(year)));
	    }

	    return financialYearOpt;
	}
	
	public void resetAction(){
		getManagedBean().setFinancialYear(null);
		getManagedBean().setProcurementTypeCode(null);
		resetSearchCriteriaValues();
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		retrieveReportValueFromDb();
		if(getManagedBean().getMinistry() != null){
			params.put(FlReportConst.FL_RPT_MINISTRY, getManagedBean().getMinistry().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtjGroup() != null) {
			params.put(FlReportConst.FL_RPT_KUMPULAN, getManagedBean().getPtjGroup().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtj() != null) {
			params.put(FlReportConst.FL_RPT_PTJ_NAME, getManagedBean().getPtj().getOrgProfileId().toString());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPanelName())) {
			params.put(FlReportConst.FL_RPT_CC_PANEL_NAME, getManagedBean().getPanelName());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getFinancialYear())) {
			params.put(FlReportConst.FL_RPT_FN_YEAR, getManagedBean().getFinancialYear());
		}
		if(getManagedBean().getStatusDescId() != null){
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if(getManagedBean().getProcurementTypeCode() != null){
			params.put(FlReportConst.FL_RPT_PROCUREMENT_TYPE, getManagedBean().getProcurementTypeCode().toString());
		} 
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		
		generateFlReport(params);
		
	}
	
	public SelectItem[] getStatusDropdownValuesForItemThreshold(String docType) {
		log.info("**getStatusDropdownValuesForItemThreshold**");

		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(ModuleCodeConst.ORDER_FULFILLMENT, docType, null);
			List<PmStatusDesc> statusDescsItemThreshold = new ArrayList<PmStatusDesc>();

			for (PmStatusDesc statusDesc : statusDescs) {
				if(statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CLOSED)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CLOSED_MANUAL_PAYMENT_1GFMAS)){
					statusDescsItemThreshold.add(statusDesc);
				}
			}
				
			items = new SelectItem[statusDescsItemThreshold.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDescYep : statusDescsItemThreshold) {
					items[i++] = new SelectItem(statusDescYep.getPmStatus()
							.getStatusId(), statusDescYep.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

}
