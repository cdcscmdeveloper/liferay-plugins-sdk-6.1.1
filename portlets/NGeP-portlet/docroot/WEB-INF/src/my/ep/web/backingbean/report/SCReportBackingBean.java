package my.ep.web.backingbean.report;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.constant.report.CommonMethod;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="scReportBackingBean")
@ViewScoped
public class SCReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	/*
	 *Enable SC module criteria by report name 
	 */
	private List<SelectItem> monthList = new ArrayList<SelectItem>();
	private List<String> yearList = new ArrayList<String>();
	
	public List<String> getYearList() {
		return yearList;
	}
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
	public List<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<SelectItem> monthList) {
		this.monthList = monthList;
	}
	
	public void criteriaVariablesByReportBySC(ReportsGenerateBean rptGenBean,	List<String> authoriztionList){
	
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
	        case ReportConst.SC_QUOT_TENDER_OR_RPT_VAL:
	        	//generateYearList();
	        	//generateMonthList();
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	rptGenBean.setScDeskOfficerView(true);
	        	rptGenBean.setScPtjView(true);
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);
	        	
	            break;
	        default:
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
	            //break;
		}		
	}
	public List<SelectItem> generateMonthList(){
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel("January");
		selectItem1.setValue("01");
		getMonthList().add(selectItem1);			
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel("February");
		selectItem2.setValue("02");
		getMonthList().add(selectItem2);
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel("March");
		selectItem3.setValue("03");
		getMonthList().add(selectItem3);
		SelectItem selectItem4= new SelectItem();
		selectItem4.setLabel("April");
		selectItem4.setValue("04");
		getMonthList().add(selectItem4);			
		SelectItem selectItem5= new SelectItem();
		selectItem5.setLabel("May");
		selectItem5.setValue("05");
		getMonthList().add(selectItem5);
		SelectItem selectItem6= new SelectItem();
		selectItem6.setLabel("June");
		selectItem6.setValue("06");
		getMonthList().add(selectItem6);
		SelectItem selectItem7= new SelectItem();
		selectItem7.setLabel("July");
		selectItem7.setValue("07");
		getMonthList().add(selectItem7);			
		SelectItem selectItem8= new SelectItem();
		selectItem8.setLabel("August");
		selectItem8.setValue("08");
		getMonthList().add(selectItem8);
		SelectItem selectItem9= new SelectItem();
		selectItem9.setLabel("September");
		selectItem9.setValue("09");
		getMonthList().add(selectItem9);
		SelectItem selectItem10= new SelectItem();
		selectItem10.setLabel("October");
		selectItem10.setValue("10");
		getMonthList().add(selectItem10);			
		SelectItem selectItem11= new SelectItem();
		selectItem11.setLabel("November");
		selectItem11.setValue("11");
		getMonthList().add(selectItem11);
		SelectItem selectItem12= new SelectItem();
		selectItem12.setLabel("December");
		selectItem12.setValue("12");
		getMonthList().add(selectItem12);
		return getMonthList();
	}
	public List<String> generateYearList(){
		 for (int i = 1950; i <= 2150; i++) {
			 getYearList().add(Integer.toString(i));
		 }
		 return getYearList(); 
	}
}
