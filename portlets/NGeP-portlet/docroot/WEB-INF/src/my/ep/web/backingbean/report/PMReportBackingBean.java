/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
// TODO: Auto-generated Javadoc

/**
 * The Class PMReportBackingBean.
 *
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="pmReportBackingBean")
@ViewScoped
public class PMReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{
	
	
	/**
	 * enableCriteriaVariablesByReportByPM - Enable criteria variables by report by PROFILE_MANAGEMENT.
	 *
	 * @param rptGenBean the rpt gen bean
	 * @param authoriztionList the authoriztion list
	 */
	public void enableCriteriaVariablesByReportByPM(ReportsGenerateBean rptGenBean,	List<String> authoriztionList){
		switch (getManagedBean().getReportValue().trim()) {
		
		case ReportConst.PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL:		        	
				rptGenBean.setPmServiceTypeView(true);
				//rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setPmStatusView(true);
				rptGenBean.setPmFinancialOrgNameView(true);
				rptGenBean.setPmBusinessRegNoView(true);
            break;		
		
		default: 
        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
            //break;
		}
	}	
}
