/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report.dp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.managedbean.report.dp.LetterOfAcceptanceRptManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class LetterOfAcceptanceRptBackingBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name = "loaRptBackingBean")
@ViewScoped
public class LetterOfAcceptanceRptBackingBean extends DpReportBackingBean<LetterOfAcceptanceRptManagedBean>
	implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(LetterOfAcceptanceRptBackingBean.class);
	
	public void generateReport(LinkedHashMap<String, String> params){
		generateDpReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setStatus(null);
		getManagedBean().setDocNo(null);
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		log.info("Date From>>>>>>" + getManagedBean().getDateFrom());
		log.info("Date To>>>>>>" + getManagedBean().getDateTo());
		log.info("Status>>>>>>" + getManagedBean().getStatus());
		log.info("Doc No>>>>>>" + getManagedBean().getDocNo());
		log.info("SQ No>>>>>>" + getManagedBean().getSimpleQuoteNo());
		log.info("SQ Type>>>>>>" + getManagedBean().getSimpleQuoteType());
		
		validateCriteria();
		retrieveReportValueFromDb();
		if (getManagedBean().getDateFrom() != null) {
			params.put(DpReportConst.PARAM_CREATED_FROM_DATE, df.format(getManagedBean().getDateFrom()));
		}
		if (getManagedBean().getDateTo() != null) {
			params.put(DpReportConst.PARAM_CREATED_TO_DATE, df.format(getManagedBean().getDateTo()));
		}
		if (getManagedBean().getStatus() != null) {
			params.put(DpReportConst.PARAM_STATUS, getManagedBean().getStatus().toString());
		}
		if (getManagedBean().getSimpleQuoteType() != null) {
			params.put(DpReportConst.PARAM_SQ_TYPE, getManagedBean().getSimpleQuoteType());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getDocNo())) {
			params.put(DpReportConst.PARAM_SQ_NO, getManagedBean().getSimpleQuoteNo());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getDocNo())) {
			params.put(DpReportConst.PARAM_LOA_NUM, getManagedBean().getDocNo());
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
	 * On change sc simple quote type view.
	 */
	public void onChangeScSimpleQuoteTypeView() {
	}

	
	/**
	 * On supplier change
	 */
	public void onSupplierChange() {
	}

	@Override
	public void validateCriteria() {
		if(this.getManagedBean().getDateFrom() == null
				&& this.getManagedBean().getDateTo() == null
				&& this.getManagedBean().getStatus() == null
				&& this.getManagedBean().getSimpleQuoteType() == null
				&& this.getManagedBean().getSelectedOrgProfile() == null
				&& StringUtils.isEmpty(this.getManagedBean().getDocNo())){
			FacesContext.getCurrentInstance().validationFailed();
			throw new EpException(MessageConst.ERROR_780003);
		}
	}
}
