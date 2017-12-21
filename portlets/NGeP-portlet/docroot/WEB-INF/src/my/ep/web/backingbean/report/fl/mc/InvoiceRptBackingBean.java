/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl.mc;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.mc.InvoiceRptManagedBean;
/**
 * Backing Bean for InvoiceRpt.
 *
 * @author Jimmy
 * @since 16/5/2017
 */
@ManagedBean(name = "mcInvRptBB")
@SessionScoped
public class InvoiceRptBackingBean extends FlReportBackingBean<InvoiceRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(InvoiceRptBackingBean.class);

	public InvoiceRptBackingBean() {
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
		if (StringUtils.isNotBlank(getManagedBean().getContractNo())) {
			params.put(FlReportConst.FL_RPT_CONTRACT_NO, getManagedBean().getContractNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPanelName())) {
			params.put(FlReportConst.FL_RPT_CC_PANEL_NAME, getManagedBean().getPanelName());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPhysicalContractNo())) {
			params.put(FlReportConst.FL_RPT_PHY_CONTRACT_NO, getManagedBean().getPhysicalContractNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPhysicalLoaNo())) {
			params.put(FlReportConst.FL_RPT_PHY_LOA_NO, getManagedBean().getPhysicalLoaNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			params.put(FlReportConst.FL_RPT_SST_NO, getManagedBean().getLoaNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getSupplierInvNo())) {
			params.put(FlReportConst.FL_RPT_SUPPL_INV_NO, getManagedBean().getSupplierInvNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getInvNo())) {
			params.put(FlReportConst.FL_RPT_INV_NO, getManagedBean().getInvNo());
		}
		if(getManagedBean().getStatusDescId() != null){
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		} else if(getManagedBean().getSupplierId() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSupplierId());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		
		generateFlReport(params);
		
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		if(getManagedBean().isSupplier() || hasAuditGenUserRole()){
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
		
		resetSearchCriteriaValues();
	}
}