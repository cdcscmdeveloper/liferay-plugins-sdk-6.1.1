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
import my.ep.web.managedbean.report.dp.PidSummaryRptManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class PidSummaryRptBackingBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name = "pidSummaryRptBackingBean")
@ViewScoped
public class PidSummaryRptBackingBean extends DpReportBackingBean<PidSummaryRptManagedBean>
	implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(PidSummaryRptBackingBean.class);
	
	public void generateReport(LinkedHashMap<String, String> params){
		generateDpReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setStatus(null);
		getManagedBean().setDocNo(null);
		getManagedBean().setContractNo(null);
		getManagedBean().setContractType(null);
		getManagedBean().setContractName(null);
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
		log.info("Contract No>>>>>>" + getManagedBean().getContractNo());
		log.info("Contract Type>>>>>>" + getManagedBean().getContractType());
		log.info("Contract Name>>>>>>" + getManagedBean().getContractName());
		
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
		if (StringUtils.isNotEmpty(getManagedBean().getDocNo())) {
			params.put(DpReportConst.PARAM_PID_NO, getManagedBean().getDocNo());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getContractType())) {
			params.put(DpReportConst.PARAM_CONTRACT_TYPE, getManagedBean().getContractType());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getContractNo())) {
			params.put(DpReportConst.PARAM_CONTRACT_NO, getManagedBean().getContractNo());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getContractName())) {
			params.put(DpReportConst.PARAM_CONTRACT_NAME, getManagedBean().getContractName());
		}
		return params;
	}

	@Override
	public void validateCriteria() {
		if(this.getManagedBean().getDateFrom() == null
				&& this.getManagedBean().getDateTo() == null
				&& this.getManagedBean().getStatus() == null
				&& StringUtils.isEmpty(this.getManagedBean().getDocNo())
				&& StringUtils.isEmpty(this.getManagedBean().getContractType())
				&& StringUtils.isEmpty(this.getManagedBean().getContractNo())
				&& StringUtils.isEmpty(this.getManagedBean().getContractName())){
			FacesContext.getCurrentInstance().validationFailed();
			throw new EpException(MessageConst.ERROR_780003);
		}
	}
}
