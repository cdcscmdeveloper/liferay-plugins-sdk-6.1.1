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
import my.ep.web.managedbean.report.fl.cc.ContractRequestRptManagedBean;
/**
 * Backing Bean for ContractRequestRpt.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "crMcRptBB")
@SessionScoped
public class ContractRequestRptBackingBean extends FlReportBackingBean<ContractRequestRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(ContractRequestRptBackingBean.class);

	public ContractRequestRptBackingBean() {
		if(getManagedBean().getMinistry() == null){
			defaultPtjAndMinistryValues();
		}
		if(getManagedBean().getFromDate() == null){
			setDefaultDateFromAndDateTo();
		}
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		
		retrieveReportValueFromDb();
		params.put(FlReportConst.FL_RPT_FROM_DATE, df.format(getManagedBean().getFromDate()));
		params.put(FlReportConst.FL_RPT_TO_DATE, df.format(getManagedBean().getToDate()));
		checkOneYearDifferent(getManagedBean().getFromDate(), getManagedBean().getToDate());
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
		if (StringUtils.isNotBlank(getManagedBean().getContractRequestNo())) {
			params.put(FlReportConst.FL_RPT_CONTRACT_REQ_NO, getManagedBean().getContractRequestNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			params.put(FlReportConst.FL_RPT_SST_NO, getManagedBean().getLoaNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			params.put(FlReportConst.FL_RPT_SST_NO, getManagedBean().getLoaNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		
		generateFlReport(params);
		
	}
	
	
}