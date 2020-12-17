/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl.po;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.po.PurLateDeliveryRptManagedBean;
/**
 * Backing Bean for PurLateDeliveryRpt.
 *
 * @author NORIZAN
 * @since 29/5/2017
 */
@ManagedBean(name = "purLdRptBB")
@SessionScoped
public class PurLateDeliveryRptBackingBean extends FlReportBackingBean<PurLateDeliveryRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PurLateDeliveryRptBackingBean.class);

	public PurLateDeliveryRptBackingBean() {
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
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPanelName())) {
			params.put(FlReportConst.FL_RPT_CC_PANEL_NAME, getManagedBean().getPanelName());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getDeliveryOrderNo())) {
			params.put(FlReportConst.FL_RPT_DELI_ORD_NO, getManagedBean().getDeliveryOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getSupplierDoRefNo())) {
			params.put(FlReportConst.FL_RPT_SUP_DELI_ORD_NO, getManagedBean().getSupplierDoRefNo());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		} else if(getManagedBean().getSupplierId() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSupplierId());
		}
		if(getManagedBean().getReceivingOfficer() != null){
			params.put(FlReportConst.FL_RPT_REC_OFF, getManagedBean().getReceivingOfficer());
		}
		if(getManagedBean().getAcknowledgeOfficer() != null){
			params.put(FlReportConst.FL_RPT_ACKNOWLEDGE_NAME, getManagedBean().getAcknowledgeOfficer());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		
		generateFlReport(params);
		
	}
	
	/**
	 * retrieve receiving officer list
	 */
	public List<SelectItem> retrieveReceivingOfficerList(){
		if(getManagedBean().getPtj() != null){
			return getReceivingOfficerList(getFacesScopes().getUserSessionBean().getUserId(),getManagedBean().getPtj().getOrgProfileId());
		}
		return null;
	}
	
	/**
	 * retrieve acknowledge officer list
	 */
	public List<SelectItem> retrieveAcknowledgeOfficerList(){
		if(getManagedBean().getPtj() != null){
			return getAcknowledgeOfficerList(getFacesScopes().getUserSessionBean().getUserId(),getManagedBean().getPtj().getOrgProfileId());
		}
		return null;
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		getManagedBean().setDeliveryOrderNo(null);
		getManagedBean().setSupplierDoRefNo(null);
		resetSearchCriteriaValues();
	}
	
}