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

package my.ep.web.backingbean.report.qt;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.ep.biz.dto.rpt.ReportTempInfoReqDTO;
import my.ep.biz.dto.rpt.ReportTempInfoResDTO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.biz.service.rpt.IReportTempInfoSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.rpt.ReportTempInfo;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.qt.QtReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class QtReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
public abstract class QtReportBackingBean <T extends QtReportManagedBean>
extends ReportBaseBackingBean<T> {
private static final long serialVersionUID = 1L;
private static final Logger log = EpLogger
	.getLogger(QtReportBackingBean.class);


/**
 * retrive reports dropdown data from database.
 */
public void reportOnChange() {
	if (!StringUtils.isBlank(getManagedBean().getReportCode())) {
		retrieveReportValueFromDb();
	}
}

/**
 * quotationNumberValidation - Quotation number validation.
 */
public void quotationNumberValidation() {
	log.info("ReportBackingBean-quotationNumberValidation"+this.getManagedBean().getQuotationNumner());
	if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumner())) {
		specialCharValidation();
	}else{
		this.getManagedBean().setQuotationId(null);
	}
}


/**
 * specialCharValidation - Special char validation.
 */
public void specialCharValidation() {
	log.info("ReportBackingBean-specialCharValidation"+this.getManagedBean().getQuotationNumner());
	String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
	Pattern pattern = Pattern.compile(ptString);
	Matcher match;
	
		match = pattern.matcher(this.getManagedBean().getQuotationNumner());

	if (match.matches()) {
		
			getQuotationNumber();
		

	} else {
		this.getManagedBean().setQuotationNumner(null);
		this.getManagedBean().setQuotationId(null);
		throw new EpException(MessageConst.ERROR_REPORT_780006);
	}
}


/**
 * getQuotationNumber - Gets the quotation number.
 * 
 * @return the quotationNumber
 */
public void getQuotationNumber() {
	log.info("ReportBackingBean-getQuotationNumber"+this.getManagedBean().getQuotationNumner());
	QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
	// log.info("QtNo="+this.getScQuotationNo());
	if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumner())) {
		tenderReqDTO.setQtTndrNo(this.getManagedBean().getQuotationNumner().toUpperCase());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQtByQtNumber(tenderReqDTO);
		ScQt scqt = resDTO.getQtDetail();
		if (scqt != null) {
			if (scqt.getQtId() != null) {
				// log.info("QtId="+scqt.getQtId());
				this.getManagedBean().setQuotationId(scqt.getQtId().toString());
			} else {
				this.getManagedBean().setQuotationNumner(null);
				this.getManagedBean().setQuotationId(null);
				throw new EpException(MessageConst.ERROR_REPORT_780005);
			}
		} else {
			this.getManagedBean().setQuotationNumner(null);
			this.getManagedBean().setQuotationId(null);
			throw new EpException(MessageConst.ERROR_REPORT_780005);
		}
	}
}


/**
 * generateSCReport - Generate SOURCING(SUPPLIER SELECTION and DIRECT
 * PURCHASE) module reports.
 */


public void generateSCReport(LinkedHashMap<String, String> params) {
	IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
					IRPTRequestSO.class);
	log.info("generate reportCode"+getManagedBean()
			.getReportCode());
	log.info("generate reportName"+getManagedBean()
			.getReportName());
	params.put(QtReportConst.PARAM_KEY_REPORT, getManagedBean()
			.getReportName());
	params.put(QtReportConst.PARAM_REPORT_CODE, getManagedBean()
			.getReportCode());
	params.put(QtReportConst.PARAM_USER_ID, getFacesScopes()
			.getUserSessionBean().getUserId().toString());

	RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
	reqDTO.setDefaultOutputType(this.getManagedBean()
			.getDefaultOutputType());
	reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
	log.info("QtId"+this.getManagedBean().getQuotationId());
	reqDTO.setParametersMap(params);
	try {
		
		RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
		getPopUpMsg(String.valueOf(resDTO.getRequestId()));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public FacesScopes getFacesScopes() {
	FacesScopes facesScopes = new FacesScopes();
	return facesScopes;
}

public BigDecimal getEstimatedAmtByQtID(String qtId) {

	BigDecimal quotationId = new BigDecimal(
			qtId != null ? qtId.replaceAll(",",
					"") : null);
BigDecimal estimatedAmount=	decryptionByQtId(quotationId, this.getManagedBean().getReportCode());
return estimatedAmount;

}

private BigDecimal decryptionByQtId(BigDecimal qtId,
		String reportCode) {
	log.info("decryptionByQtId");
	IQTDetailSO QtDetailsSO;	

	try {
		QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

		tenderReqDTO.setQtId(qtId.longValue());
		BigDecimal estimatedAmount = QtDetailsSO
				.getdecryptedEstAmount(tenderReqDTO.getQtId());
		
		return estimatedAmount;
		
	} catch (Exception e) {
		throw (e);
	} finally {
		QtDetailsSO = null;
	}

}


private ReportTempInfoResDTO toSaveDecryptedAmountWithQNoAndQId(
		BigDecimal qtId, BigDecimal estimatedAmount) {
	IReportTempInfoSO reportTempSO;
	reportTempSO = EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
					IReportTempInfoSO.class);

	ReportTempInfo rptTmp = new ReportTempInfo();
	rptTmp.setReportCode(this.getManagedBean().getReportCode());		
	rptTmp.setQtId(qtId.toString());
	rptTmp.setEstimatedAmt(estimatedAmount != null ? estimatedAmount
			.toString() : null);		

	ReportTempInfoReqDTO reqDTO = new ReportTempInfoReqDTO();
	reqDTO.setReportTempInfo(rptTmp);

	ReportTempInfoResDTO resDTO = reportTempSO
			.saveDecryptedAmountWithQuotationNoAndQuotationId(reqDTO);
	reportTempSO=null;
	return resDTO;
}

public abstract void generateReport(LinkedHashMap<String,String> params);

public abstract void resetSearch();

public abstract LinkedHashMap<String,String> getparams();
}
