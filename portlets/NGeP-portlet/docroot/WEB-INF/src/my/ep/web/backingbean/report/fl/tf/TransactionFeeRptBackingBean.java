package my.ep.web.backingbean.report.fl.tf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.tf.TransactionFeeRptManagedBean;

/**
 * Backing Bean for Transaction Fee Rpt.
 *
 * @author razin
 * @since 08/06/2021
 */
@ManagedBean(name = "tfRptBB")
@SessionScoped
public class TransactionFeeRptBackingBean extends FlReportBackingBean<TransactionFeeRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = EpLogger
			.getLogger(TransactionFeeRptBackingBean.class);
	
	public TransactionFeeRptBackingBean() {}
	
	public void resetAction(){
		getManagedBean().setAgOff(null);
		getManagedBean().setCheckSFOneOff(false);
		getManagedBean().setCheckSFPeriodic(false);
		getManagedBean().setDocumentType(null);
		setDefaultTransactionDate();
		
		resetSearchCriteriaValues();
	}
	
	public void setDefaultTransactionDate() {
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
		getManagedBean().setTransactionFromDate(fromDate.getTime());
		getManagedBean().setTransactionToDate(new Date());
   }
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		
		retrieveReportValueFromDb();
		
		//add ag office name
		if (getManagedBean().getAgOff().getOffDesc() !=null ) {
			params.put(FlReportConst.FL_RPT_AG_OFFICE, getManagedBean().getAgOff().getOffCode());
		}
		
		//add PSF one off
		if (getManagedBean().isCheckSFOneOff() == true ) {
			params.put(FlReportConst.FL_RPT_PSF_ONE_OFF, "Y");
		}else{
			params.put(FlReportConst.FL_RPT_PSF_ONE_OFF, "N");
		}
		
		//add PSF period
		if (getManagedBean().isCheckSFPeriodic() == true) {
			params.put(FlReportConst.FL_RPT_PSF_PERIOD, "Y");
		}else{
			params.put(FlReportConst.FL_RPT_PSF_PERIOD, "N");
		}
		
		params.put(FlReportConst.FL_RPT_TRANSACTION_FROM_DATE, df.format(getManagedBean().getTransactionFromDate()));
		params.put(FlReportConst.FL_RPT_TRANSACTION_TO_DATE, df.format(getManagedBean().getTransactionToDate()));
		checkOneYearDifferent(getManagedBean().getTransactionFromDate(), getManagedBean().getTransactionToDate());
		
		generateFlReport(params);
	}

}
