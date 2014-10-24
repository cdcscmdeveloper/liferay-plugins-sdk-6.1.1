package my.ep.web.backingbean.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.pp.common.CommonMethod;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="scReportBackingBean")
@ViewScoped
public class SCReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	/*
	 *Enable SC module criteria by report name 
	 */
	public void criteriaVariablesByReportBySC(ReportsGenerateBean rptGenBean){
	
		switch (getManagedBean().getReportValue()) {
		 	case ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL:
		 		rptGenBean.setScQuotationNoView(true);		 		
	            break; 
	        case ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL:	        	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPPP1_PEMATUHAN_OC_RPT_NAME));
	            break;
	        case ReportConst.PDDW_OC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.AKPP_PEMATUHAN_OC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.SPPSAP2_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.AKPT_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.TECSPEC_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.SPPSAP2_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.AKPT_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.HARGA_TAWARAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_FINRS_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL:	        	
	            break;
	        case ReportConst.PDW_EC_RPT_VAL:	        	
	            break;
	        case ReportConst.SPPSAP2_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.AKPTPH_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.TECSPEC_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.HARGA_TAWARAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.OVRL_FINRS_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;	        	 
	        case ReportConst.PSDAK_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.PSDABK_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        case ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);				
	            break;
	        default:
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
	            //break;
		}		
	}	
}
