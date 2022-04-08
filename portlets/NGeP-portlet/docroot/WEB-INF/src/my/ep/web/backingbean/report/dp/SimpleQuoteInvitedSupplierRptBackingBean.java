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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.dp.SimpleQuoteInvitedSupplierRptManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class SimpleQuoteInvitedSupplierRptBackingBean.
 * 
 * @author Manga Garikapati.
 * @since 08.06.2017
 */
@ManagedBean(name = "simpleQuoteInviteSupplierRptBackingBean")
@ViewScoped
public class SimpleQuoteInvitedSupplierRptBackingBean extends
		DpReportBackingBean<SimpleQuoteInvitedSupplierRptManagedBean> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SimpleQuoteInvitedSupplierRptBackingBean.class);

	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("SimpleQuoteInvitedSupplierRptBackingBean-generateReport");
		/*for (Entry<String, String> entry : params.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
		}*/
		generateDpReport(params);
	}

	@Override
	public void resetSearch() {
		this.getManagedBean().setSimpleQuoteNum(null);

	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		
		validateCriteria();
		if (StringUtils.isNotEmpty(getManagedBean().getSimpleQuoteNum())) {
			simpleQuoteNoValidation();
			params.put(DpReportConst.PARAM_SQ_NO, getManagedBean()
					.getSimpleQuoteNum());

		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.getInstance().get(Calendar.YEAR), 0, 1, 0, 0, 0);
		//fromDate.set(Calendar.MILLISECOND, 0);
		Date fromCreatedDate = fromDate.getTime();
		log.info("fromDate" + dateFormat.format(fromCreatedDate));
		params.put(DpReportConst.PARAM_CREATED_FROM_DATE,
				dateFormat.format(fromCreatedDate));
		Date toDate = new Date();
		log.info("toDate" + dateFormat.format(toDate));
		params.put(DpReportConst.PARAM_CREATED_TO_DATE,
				dateFormat.format(toDate));
		return params;
	}

	/**
	 * simpleQuoteNoValidation - Simple quote no validation.
	 */
	public void simpleQuoteNoValidation() {
		log.info("SimpleQuoteInvitedSupplierRptBackingBean-simpleQuoteNoValidation");
		// getIisDPReportsStatus();
		
		if (StringUtils.isNotEmpty(getManagedBean().getSimpleQuoteNum())) {
			specialCharValidation();
		}
		
	}

	/**
	 * specialCharValidation - Special char validation.
	 */
	private void specialCharValidation() {
		log.info("SimpleQuoteInvitedSupplierRptBackingBean-specialCharValidation");
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		Matcher match;

		match = pattern.matcher(getManagedBean().getSimpleQuoteNum());

		if (match.matches()) {

			 retrieveSimpleQuoteNo();
		}

		else {
			getManagedBean().setSimpleQuoteNum(null);
			throw new EpException(MessageConst.ERROR_REPORT_780021);
		}
	}

	/**
	 * retrieveSimpleQuoteNo - Retrieve simple quote no.
	 * 
	 * @return the simpleQuoteNoList
	 */
	private List<String> retrieveSimpleQuoteNo() {
		log.info("SimpleQuoteInvitedSupplierRptBackingBean-retrieveSimpleQuoteNo");
		List<String> simpleQuoteNoList = null;
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> smplQtNoList = reportSO
					.retrieveSimpleQuoteNo(getManagedBean().getSimpleQuoteNum());
			if (smplQtNoList.size() > 0) {
				simpleQuoteNoList = new ArrayList<String>();
				for (int i = 0; i < smplQtNoList.size(); i++) {
					simpleQuoteNoList.add(smplQtNoList.get(i));
				}
			} else {
				getManagedBean().setSimpleQuoteNum(null);
				throw new EpException(MessageConst.ERROR_REPORT_780021);
			}
		}
		return simpleQuoteNoList;
	}

	@Override
	public void validateCriteria() {
		if(StringUtils.isEmpty(this.getManagedBean().getSimpleQuoteNum())){
			FacesContext.getCurrentInstance().validationFailed();
			throw new EpException(MessageConst.ERROR_780003);
		}
	}

}
