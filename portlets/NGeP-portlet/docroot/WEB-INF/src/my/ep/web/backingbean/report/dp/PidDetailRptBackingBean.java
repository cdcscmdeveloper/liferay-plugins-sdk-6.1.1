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
import my.ep.web.managedbean.report.dp.PidDetailRptManagedBean;

import org.apache.commons.collections.CollectionUtils;//#29558
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class PidDetailRptBackingBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name = "pidDetailRptBackingBean")
@ViewScoped
public class PidDetailRptBackingBean extends DpReportBackingBean<PidDetailRptManagedBean>
	implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(PidDetailRptBackingBean.class);
	
	public void generateReport(LinkedHashMap<String, String> params){
		generateDpReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setScStatusManyMenu(null);//#29558
		getManagedBean().setScStatusManyMenuTemp(null);//#29558
		getManagedBean().setScRequisitioner(null);//#29558
		getManagedBean().setContractType(null);//#29558
		getManagedBean().setDocNo(null);
		getManagedBean().setContractNo(null);
		getManagedBean().setContractType(null);
		getManagedBean().setContractName(null);
		getManagedBean().setPidGroup(null);
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
		log.info("Group>>>>>>" + getManagedBean().getPidGroup());
		
		validateCriteria();
		retrieveReportValueFromDb();
		if (getManagedBean().getDateFrom() != null) {
			params.put(DpReportConst.PARAM_CREATED_FROM_DATE, df.format(getManagedBean().getDateFrom()));
		}
		if (getManagedBean().getDateTo() != null) {
			params.put(DpReportConst.PARAM_CREATED_TO_DATE, df.format(getManagedBean().getDateTo()));
		}
		// 29558 - Start (Harun) Change option from single status to multiple
		if(CollectionUtils.isNotEmpty(getManagedBean().getScStatusManyMenu())){
			String status = StringUtils.join(getManagedBean().getScStatusManyMenu(),",");
			params.put(DpReportConst.PARAM_STATUS, status.replaceAll(",$",""));
		}
		// 29558 - End (Harun)
		if (getManagedBean().getPidGroup() != null) {
			params.put(DpReportConst.PARAM_GROUP, getManagedBean().getPidGroup());
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
		//29558 - Start (Harun) fixing sonarlint issues
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			log.info("Org Profile>>>>>>" + getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			params.put(DpReportConst.PARAM_SUPPLIER, 
					getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		//29558 - End
		return params;
	}
	
	/**
	 * On supplier change
	 */
	public void onSupplierChange() {
	}

	@Override
	//29558 - Start (Harun) fixing sonarlint issues
	public void validateCriteria() {
	    if (allCriteriaNullOrEmpty()) {
	        FacesContext.getCurrentInstance().validationFailed();
	        throw new EpException(MessageConst.ERROR_780003);
	    }
	}

	private boolean allCriteriaNullOrEmpty() {
	    return isDateRangeNull() && isStatusAndTempNull() && isOtherCriteriaNull() && isContractEmpty();
	}

	private boolean isDateRangeNull() {
	    return this.getManagedBean().getDateFrom() == null && this.getManagedBean().getDateTo() == null;
	}

	private boolean isStatusAndTempNull() {
	    return this.getManagedBean().getScStatusManyMenu() == null && this.getManagedBean().getScStatusManyMenuTemp() == null;
	}

	private boolean isOtherCriteriaNull() {
	    return this.getManagedBean().getPidGroup() == null &&
	    	   this.getManagedBean().getSelectedOrgProfile() == null && 
	           StringUtils.isEmpty(this.getManagedBean().getDocNo());
	}
	
	private boolean isContractEmpty() {
	    return  StringUtils.isEmpty(this.getManagedBean().getContractType()) &&
	    		StringUtils.isEmpty(this.getManagedBean().getContractNo()) && 
	    		StringUtils.isEmpty(this.getManagedBean().getContractName());
	    	   
	}
	//29558 - End
}
