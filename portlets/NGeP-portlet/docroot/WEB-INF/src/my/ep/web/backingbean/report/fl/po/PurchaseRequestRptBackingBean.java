/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl.po;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.fl.po.PurchaseRequestRptManagedBean;
/**
 * Backing Bean for PurchaseRequestRpt.
 *
 * @author NORIZAN
 * @since 25/5/2017
 */
@ManagedBean(name = "prRptBB")
@SessionScoped
public class PurchaseRequestRptBackingBean extends FlReportBackingBean<PurchaseRequestRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PurchaseRequestRptBackingBean.class);

	public PurchaseRequestRptBackingBean() {
		if(getManagedBean().getMinistry() == null){
			defaultPtjAndMinistryValues();
		}
		if(getManagedBean().getFromDate() == null){
			setDefaultDateFromAndDateTo();
		}
	}
	
	/**
	 * generate report
	 */
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		
		retrieveReportValueFromDb();
		params.put(FlReportConst.FL_RPT_FROM_DATE, df.format(getManagedBean().getFromDate()));
		params.put(FlReportConst.FL_RPT_TO_DATE, df.format(getManagedBean().getToDate()));
		checkOneYearDifferent(getManagedBean().getFromDate(), getManagedBean().getToDate());
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
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseRequestNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_REQUEST, getManagedBean().getPurchaseRequestNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if(getManagedBean().getStatusDescId() != null){
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if(getManagedBean().getRequisitioner() != null){
			params.put(FlReportConst.FL_RPT_REQUI_NAME, getManagedBean().getRequisitioner());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		// #27476 start
		if (StringUtils.isNotBlank(getManagedBean().getProcumentType())) {
			params.put(FlReportConst.FL_RPT_PROCUMENT_TYPE_CODE, getManagedBean().getProcumentType());
		}
		// #27476 end
		
		generateFlReport(params);
	}
	
	/**
	 * retrieve requisitioner list
	 */
	public List<SelectItem> retrieveRequisitionerList(){
		if(getManagedBean().getPtj() != null){
			return getRequisitionerList(getFacesScopes().getUserSessionBean().getUserId(),getManagedBean().getPtj().getOrgProfileId());
		}
		return null;
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		getManagedBean().setPurchaseRequestNo(null);
		resetSearchCriteriaValues();
	}
	
}