/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl.yep;

import java.text.SimpleDateFormat;
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

import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.fl.yep.YearEndRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * Backing Bean for PurchaseRequestRpt.
 *
 * @author NORIZAN
 * @since 25/5/2017
 */
@ManagedBean(name = "yepRptBB")
@SessionScoped
public class YearEndRptBackingBean extends FlReportBackingBean<YearEndRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(YearEndRptBackingBean.class);

	public YearEndRptBackingBean() {
		if(getManagedBean().getMinistry() == null){
			defaultPtjAndMinistryValues();
		}
		if(getManagedBean().getFromDate() == null){
			setDefaultDateFromAndDateTo();
		}
		
		if(getManagedBean().getContractFromDate() == null){
			setDefaultContractDate();
		}
		
		getManagedBean().setContractFlag(false);
		getManagedBean().setAp58Flag(true);
		getManagedBean().setCfwdFlag(true);
		getManagedBean().setCmpFlag(true);
		getManagedBean().setNatFlag(true);
		getManagedBean().setCheckAP58(false);
		getManagedBean().setCheckCFwd(false);
		getManagedBean().setCheckCMP(false);
		getManagedBean().setCheckNAT(false);
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
		
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}	
		
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderStatus())) {
			params.put(FlReportConst.FL_RPT_PO_STATUS, getManagedBean().getPurchaseOrderStatus());
		}	
		
		if(getManagedBean().getDocumentType() != null){
			params.put(FlReportConst.FL_RPT_DOC_TYPE, getManagedBean().getDocumentType());
		}	
		
		if(getManagedBean().getSupplierCompName() != null){
			params.put(FlReportConst.FL_RPT_BC_NAME, getManagedBean().getSupplierCompName());
		}	
		
		if(getManagedBean().getPoTitle() != null){
			params.put(FlReportConst.FL_RPT_PO_TITLE, getManagedBean().getPoTitle());
		}	
		
		if(getManagedBean().getAP58A() != null){
			params.put(FlReportConst.FL_RPT_AP58A, getManagedBean().getAP58A());
		}	
		
		if(getManagedBean().getCarryForward() != null){
			params.put(FlReportConst.FL_RPT_CARRY_FORWARD, getManagedBean().getCarryForward());
		}	
		
		if(getManagedBean().getClosedManualPayment() != null){
			params.put(FlReportConst.FL_RPT_CMNL_PAYMENT, getManagedBean().getClosedManualPayment());
		}	
		
		if(getManagedBean().getNonActionableTransaction() != null){
			params.put(FlReportConst.FL_RPT_NA_TRNS, getManagedBean().getNonActionableTransaction());
		}	
		
		if(getManagedBean().getFinancialYear() != null){
			params.put(FlReportConst.FL_RPT_FN_YEAR, getManagedBean().getFinancialYear());
		}	
			
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
		
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getContractNo())) {
			params.put(FlReportConst.FL_RPT_CONTRACT_NO, getManagedBean().getContractNo());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			params.put(FlReportConst.FL_RPT_SST_NO, getManagedBean().getLoaNo());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getReferenceNo())) {
			params.put(FlReportConst.FL_RPT_REF_NO, getManagedBean().getReferenceNo());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getPhysicalContractNo())) {
			params.put(FlReportConst.FL_RPT_PHY_CONTRACT_NO, getManagedBean().getPhysicalContractNo());
		}
		
		params.put(FlReportConst.FL_RPT_CT_FROM_DATE, df.format(getManagedBean().getContractFromDate()));
		params.put(FlReportConst.FL_RPT_CT_TO_DATE, df.format(getManagedBean().getContractToDate()));
		checkOneYearDifferent(getManagedBean().getContractFromDate(), getManagedBean().getContractToDate());
		log.info("28379: params: "+params);
		
		generateFlReport(params);
	}
	
	public void onChangeDocType(){
		
		if(getManagedBean().getDocumentType() != null){
			//28379
			if(getManagedBean().getDocumentType().equals(ReportConst.DOC_TYPE_CR)){
				getManagedBean().setContractFlag(true);
				getManagedBean().setPurchaseOrderNo(null);
				getManagedBean().setPurchaseOrderStatus(null);
				getManagedBean().setPoTitle(null);
				getManagedBean().setAP58A(null);
				getManagedBean().setCarryForward(null);
				getManagedBean().setClosedManualPayment(null);
				getManagedBean().setNonActionableTransaction(null);
				
				
			} else {
				getManagedBean().setContractFlag(false);	
				getManagedBean().setPanelName(null);
				getManagedBean().setPkpNo(null);
				getManagedBean().setContractNo(null);
				getManagedBean().setLoaNo(null);
				getManagedBean().setReferenceNo(null);
				getManagedBean().setPhysicalContractNo(null);
				setDefaultContractDate();
			
			}
			
		}
		
	}
	
	public void setDefaultContractDate() {
		
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setContractFromDate(fromDate.getTime());
			getManagedBean().setContractToDate(new Date());
			
	}
	
	
	public void onChangeAp58(){
		POFlagCheck();
		if (getManagedBean().isCheckAP58()) {
			getManagedBean().setCfwdFlag(false);
			getManagedBean().setNatFlag(false);	
		} else if (getManagedBean().isCheckCMP()) {
			getManagedBean().setCfwdFlag(true);	
		} else {
			getManagedBean().setCfwdFlag(true);
			getManagedBean().setNatFlag(true);		
		}
	
	}
	
	public void onChangeCarryFow(){
		POFlagCheck();		
		if (getManagedBean().isCheckCFwd()) {
			getManagedBean().setAp58Flag(false);
			getManagedBean().setNatFlag(false);
		} else if (getManagedBean().isCheckCMP()) {
			getManagedBean().setAp58Flag(true);
		} else {
			getManagedBean().setAp58Flag(true);
			getManagedBean().setNatFlag(true);
		}
	}
	
	public void onChangeCMPay() {
		POFlagCheck();	
		if (getManagedBean().isCheckCMP()) {
			getManagedBean().setNatFlag(false);
		}  else if (!getManagedBean().isCheckAP58() && !getManagedBean().isCheckCFwd()){
			getManagedBean().setNatFlag(true);
		}
	}
	
	public void onChangeNATransaction() {
		POFlagCheck();	
		if (getManagedBean().isCheckNAT()) {
			getManagedBean().setAp58Flag(false);
			getManagedBean().setCfwdFlag(false);
			getManagedBean().setCmpFlag(false);
		} else {
			getManagedBean().setAp58Flag(true);
			getManagedBean().setCfwdFlag(true);
			getManagedBean().setCmpFlag(true);
		}
	}
	
	public void POFlagCheck() {
	if (getManagedBean().isCheckAP58()) {
		getManagedBean().setAP58A("Y");
		} else {
		getManagedBean().setAP58A(null);	
		}
			
		if (getManagedBean().isCheckCFwd()) {
		getManagedBean().setCarryForward("Y");
		} else {
		getManagedBean().setCarryForward(null);	
		}
			
		if (getManagedBean().isCheckCMP()) {
		getManagedBean().setClosedManualPayment("Y");
		} else {
		getManagedBean().setClosedManualPayment(null);	
		}
			
		if (getManagedBean().isCheckNAT()) {
		getManagedBean().setNonActionableTransaction("Y");
		} else {
		getManagedBean().setNonActionableTransaction(null);	
		}	
	}
	
	public static List<SelectItem> getFinancialYearOpt() {
		int years = 0;
		List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
		for(Integer i = 0;i < 7;i++){
			years = CommonMethod.getYear(-i);
			 financialYearOpt.add(new SelectItem(years, Integer.toString(years)));
		}
		return financialYearOpt;
	}
	
	//28379 start - new yep dropdown status
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param moduleCode
	 *            the module code
	 * @param docType
	 *            the document type
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValuesForYEP(String moduleCode,
			String docType) {
		log.info("**inside getStatusDropdownValuesForYEP**");
		
		
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			String newdocType = null;
			if(docType != null){
				if(docType == "CR"){
					newdocType = "CO";
				}else{
					newdocType = "PO";
				}
				
			}
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, newdocType, null);
			
			List<PmStatusDesc> statusDescsYep = new ArrayList<PmStatusDesc>();
			
				for (PmStatusDesc statusDesc : statusDescs) {
				
				if(statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_INVOICE)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_DAN_APPROVAL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CANCELLED)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CANCELLED_BY_YEP)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_FULFILMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PARTIAL_FL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_INVOICE)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_DAN_APPROVAL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_CANCELLED)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_CANCELLED_BY_YEP)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_REVISION_INVOICE)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_REVISION_INVOICE2)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_QUERY)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_QUERY)){
					
					statusDescsYep.add(statusDesc);
				}
			}
				
				
			items = new SelectItem[statusDescsYep.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDescYep : statusDescsYep) {
				
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
	//end 28379 
	
	/**
	 * reset action
	 */
	public void resetAction(){
		getManagedBean().setCarryForward(null);	
		getManagedBean().setAP58A(null);
		getManagedBean().setClosedManualPayment(null);
		getManagedBean().setNonActionableTransaction(null);	
		getManagedBean().setCheckAP58(false);
		getManagedBean().setCheckCFwd(false);
		getManagedBean().setCheckCMP(false);
		getManagedBean().setCheckNAT(false);
		getManagedBean().setContractFlag(false);
		getManagedBean().setAp58Flag(true);
		getManagedBean().setCfwdFlag(true);
		getManagedBean().setCmpFlag(true);
		getManagedBean().setNatFlag(true);
		
		getManagedBean().setDocumentType(null);
		getManagedBean().setPurchaseOrderStatus(null);
		getManagedBean().setPoTitle(null);
		getManagedBean().setReferenceNo(null);
		getManagedBean().setSupplierCompName(null);
		getManagedBean().setFinancialYear(null);
		setDefaultContractDate();
		
		resetSearchCriteriaValues();
	}
	
	
}