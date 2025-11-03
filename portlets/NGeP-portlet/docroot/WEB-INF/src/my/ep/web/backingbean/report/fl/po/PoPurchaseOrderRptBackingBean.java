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

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.po.PoPurchaseOrderRptManagedBean;
/**
 * Backing Bean for PoPurchaseOrderRpt.
 *
 * @author NORIZAN
 * @since 26/5/2017
 */
@ManagedBean(name = "poPurRptBB")
@SessionScoped
public class PoPurchaseOrderRptBackingBean extends FlReportBackingBean<PoPurchaseOrderRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PoPurchaseOrderRptBackingBean.class);

	public PoPurchaseOrderRptBackingBean() {
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
		//29550 - start
		if(hasFactoringUserRole()){
			onChangeFromToDate();
		}
		//29550 - end
		
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
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if(getManagedBean().getStatusDescId() != null){
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		} else if(getManagedBean().getSupplierId() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSupplierId());
		}
		if(getManagedBean().getApprover() != null){
			params.put(FlReportConst.FL_RPT_APPROVER_NAME, getManagedBean().getApprover());
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
	 * retrieve approver list
	 */
	public List<SelectItem> retrieveApproverList(){
		if(getManagedBean().getPtj() != null){
			return getApproverList(getFacesScopes().getUserSessionBean().getUserId(),getManagedBean().getPtj().getOrgProfileId());
		}
		return null;
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		resetSearchCriteriaValues();
	}
	
	//29550 - start
		public void onChangeFromToDate() {
			if(this.getManagedBean().getFromDate().after(this.getManagedBean().getToDate()) || 
					this.getManagedBean().getToDate().before(this.getManagedBean().getFromDate())){
				throw new EpException(MessageConst.ERROR_REPORT_780071);
			}
			
			getContractDuration(this.getManagedBean().getFromDate(), this.getManagedBean().getToDate());
		}
	//29550 - end
	
}