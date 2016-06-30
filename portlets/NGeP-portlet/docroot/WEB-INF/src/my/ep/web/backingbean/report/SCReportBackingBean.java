/*
 * 
 */
package my.ep.web.backingbean.report;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.constant.report.CommonMethod;
// TODO: Auto-generated Javadoc

/**
 * The Class SCReportBackingBean.
 *
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="scReportBackingBean")
@ViewScoped
public class SCReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	
	/** The month list. */
	private List<SelectItem> monthList = new ArrayList<SelectItem>();
	
	/** The year list. */
	private List<String> yearList = new ArrayList<String>();
	
	/**
	 * Gets the year list.
	 *
	 * @return the year list
	 */
	public List<String> getYearList() {
		return yearList;
	}
	
	/**
	 * Sets the year list.
	 *
	 * @param yearList the new year list
	 */
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
	
	/**
	 * Gets the month list.
	 *
	 * @return the month list
	 */
	public List<SelectItem> getMonthList() {
		return monthList;
	}
	
	/**
	 * Sets the month list.
	 *
	 * @param monthList the new month list
	 */
	public void setMonthList(List<SelectItem> monthList) {
		this.monthList = monthList;
	}
	
	/**
	 * enableCriteriaVariablesByReportBySC - Enable criteria variables by report of SOURCING(SUPPLIER SELECTION and DIRECT PURCHASE).
	 *
	 * @param rptGenBean the rpt gen bean
	 * @param authoriztionList the authoriztion list
	 * @param approverNayakTarafList the approver nayak taraf list
	 */
	public void enableCriteriaVariablesByReportBySC(ReportsGenerateBean rptGenBean,	List<String> authoriztionList,List<SelectItem> approverNayakTarafList){
			//rptGenBean.retriveStatus();
		     rptGenBean.setGenerateButton(true);
		switch (getManagedBean().getReportValue().trim()) {
		 	case ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL:
		 		rptGenBean.setScQuotationNoView(true);	
		 		rptGenBean.setExtraTwoColumnView(true);
	            break; 
	        case ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PDDW_OC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.AKPP_PEMATUHAN_OC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.SPPSAP2_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.AKPT_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.TECSPEC_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.SPPSAP2_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.AKPT_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.HARGA_TAWARAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_FINRS_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PDW_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.SPPSAP2_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.AKPTPH_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.TECSPEC_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.HARGA_TAWARAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.OVRL_FINRS_EC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.ANLSIS_EVALUATION_FC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	            
	        case ReportConst.PSDAK_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PSDABK_TEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	            break;
	        case ReportConst.SC_QUOT_TENDER_OR_RPT_VAL:
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}else{
					rptGenBean.setExtraTwoColumnView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	        	
	            break;
	        case ReportConst.SC_SUMMRY_SRC_OR_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size())>0||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}else{
					rptGenBean.setExtraTwoColumnView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_BRIF_SITE_VISIT_OR_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_QUOT_TENDER_MONITOR_OR_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_PRAPOSAL_SUB_LST_OR_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	//rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_PRICE_SCHEDULE_OR_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_LETTER_OF_ACCPT_OR_RPT_VAL:	        	
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}else{
					rptGenBean.setExtraTwoColumnView(true);
				}
	        	if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);
	            break;
	        case ReportConst.SC_COMMITTEE_DTLS_TD_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
	        	/*if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}else{
					rptGenBean.setExtraTwoColumnView(true);
				}
	        	if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}*/        	
	            break;
	        case ReportConst.SC_PROPOSAL_DTLS_PRODUCT_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);       	
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);  
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	            break;
	        case ReportConst.SC_PROPOSAL_DTLS_SERVICE_RPT_VAL:
	        	rptGenBean.setScQuotationNoView(true);
	        	rptGenBean.setScProcureAllChkView(true);
	        	rptGenBean.setScQuotationChkView(true);
	        	rptGenBean.setScTenderChkView(true);
	        	rptGenBean.setScRstrctdChkView(true);       	
	        	rptGenBean.setScStatusView(true);
	        	rptGenBean.setScCreatedFromView(true);	
	        	rptGenBean.setScPublishDateView(true);  
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER)))){
	        		rptGenBean.setScDeskOfficerView(true);
				}
	        	if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	            break;
	        case ReportConst.SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		
	    		rptGenBean.setScCommonStatusView(true);
	    		rptGenBean.setScPIDtlsPrcRptGroupView(true);
	            break;
	        case ReportConst.SC_PURCHASE_SUMMURY_ITEM_INQUIRY_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		rptGenBean.setScCommonStatusView(true);	    		
	            break;  
	        case ReportConst.SC_PURCHASE_SUMMURY_ITEM_INQUIRY_SUPPLIER_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		rptGenBean.setScCommonStatusView(true);	    		
	            break;
	        case ReportConst.SC_PURCHASE_SUMMURY_ITEM_INQUIRY_REQUISITIONER_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		rptGenBean.setScCommonStatusView(true);	    		
	            break;
	        case ReportConst.SC_PURCHASE_SUMMURY_ITEM_INQUIRY_PINO_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		rptGenBean.setScCommonStatusView(true);	    		
	            break;
	        case ReportConst.SC_LAPORAN_PURCHASE_DEFAULT_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		//rptGenBean.setScSupplierView(true);	    		
	    		rptGenBean.setScPurchaseInDefaultStatusView(true);
	    		rptGenBean.setScPIDNoView(true);
	    		rptGenBean.setScContractOrPKPTypeView(true);
	    		rptGenBean.setScContractOrPKPNoView(true);
	    		rptGenBean.setScContractOrPKPNameView(true);
	            break;
	        case ReportConst.SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);	    		
	    		rptGenBean.setScRequestNoteStatusView(true);
	    		rptGenBean.setScREQUESTNOTESUMMARYGroupView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScRequestQuoteNoView(true);
	            break;
	        case ReportConst.SC_REQUEST_NOTE_PROCUREMENT_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);	    		
	    		rptGenBean.setScCommonStatusView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScRequestQuoteNoView(true);
	            break;
	        case ReportConst.SC_REQUEST_NOTE_COMPLETE_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);	    		
	    		rptGenBean.setScRequestNoteStatusView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScRequestQuoteNoView(true);
	            break;
	        case ReportConst.SC_PURCHASE_INQ_DTL_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScPInoView(true);
	    		rptGenBean.setScCommonStatusView(true);	    		
	            break;  
	        case ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScSimpleQuoteStatusView(true);	
	    		rptGenBean.setScProcessRptQuotationNoView(true);
	            break; 
	        case ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_REQUSNR_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScCommonStatusView(true);	
	    		rptGenBean.setScProcessRptQuotationNoView(true);
	            break;
	        case ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScSimpleQuoteStatusView(true);	
	    		rptGenBean.setScProcessRptQuotationNoView(true);
	    		//rptGenBean.setScSimpleQuoteDtlGroupView(true);
	            break;
	        case ReportConst.SC_LAPORAN_PERICIAN_SQ_DTL_ITM_EST_PRICE_STS_S1_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScProcurementTypeView(true);
	    		rptGenBean.setScCommonStatusView(true);	
	    		rptGenBean.setScProcessRptQuotationNoView(true);
	            break;
	        case ReportConst.SC_S1_PURCHASE_DEFAULT_DTLS_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);	    		
	    		rptGenBean.setScPurchaseInDefaultStatusView(true);
	    		rptGenBean.setScLAPRNPURCHDEFAULTGroupView(true);
	    		rptGenBean.setScPIDNoView(true);
	    		rptGenBean.setScContractOrPKPTypeView(true);
	    		rptGenBean.setScContractOrPKPNoView(true);
	    		rptGenBean.setScContractOrPKPNameView(true);
	            break;
	        case ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL:
	        	//rptGenBean.setScSoneCrtdFromDateView(true);
	        	/*if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSimpleQuoteStatusView(true);	
	    		rptGenBean.setScProcurementTypeView(true);*/
	    		rptGenBean.setScMandatoryProcessRptQuotationNoView(true);
	    		rptGenBean.setExtraTwoColumnView(true);
	    		//rptGenBean.setScProcessRptQuotationNoView(true);	    		
	            break;
	        case ReportConst.SC_S1_LOA_RPT_VAL:
	        	rptGenBean.setScSoneCrtdFromDateView(true);
	        	if((approverNayakTarafList.size()>0)||((authoriztionList!=null)&&(authoriztionList.contains(ReportConst.RN_APPROVER)))){
	        		rptGenBean.setScRequisenerIdView(true);
	        	}
	    		if((authoriztionList!=null)&&(approverNayakTarafList.size()>0)){
	        		rptGenBean.setScPtjView(true);
	        	}
	    		rptGenBean.setScSupplierView(true);
	    		rptGenBean.setScLOANoView(true);
	    		rptGenBean.setScSimpleQuoteTypeView(true);
	    		rptGenBean.setScSOAStatusView(true);
	    		rptGenBean.setScProcessRptQuotationNoView(true);
	            break;
		   case ReportConst.SC_S1_LOA_CONTENT_RPT_VAL:
		     	rptGenBean.setScLOAContentView(true);

			break;
	        default:
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));	            
		}		
	}
	
	/**
	 * Generate month list.
	 *
	 * @param language the language
	 * @return the list
	 */
	public List<SelectItem> generateMonthList(String language){
		monthList = new ArrayList<SelectItem>();
		if((language!=null)&&(language.equalsIgnoreCase("en"))){
				
			String month = ReportConst.EMPTY_VAL;
			int monthNoInt;
			String monthNoStr= ReportConst.EMPTY_VAL;
	        DateFormatSymbols dfs = new DateFormatSymbols();
	        String[] months = dfs.getMonths();        
	       for (int i = 0; i <= 11; i++) {
	    	   monthNoInt= i+1;
	    	   if(monthNoInt<10){
	    		   monthNoStr=ReportConst.ZERO_VAL+monthNoInt;
	    	   }else{
	    		   monthNoStr=monthNoInt+ReportConst.EMPTY_VAL;
	    	   }
	            month = months[i];            
	            SelectItem selectItem= new SelectItem();
	            selectItem.setLabel(month);
	            selectItem.setValue(monthNoStr);
	            monthList.add(selectItem);
	        } 
		}else if((language!=null)&&(language.equalsIgnoreCase("ms"))){
			SelectItem selectItem= new SelectItem();
            selectItem.setLabel("Januari");
            selectItem.setValue("01");
            monthList.add(selectItem);
            SelectItem selectItem1= new SelectItem();
            selectItem.setLabel("Februari");
            selectItem.setValue("02");
            monthList.add(selectItem1);
            SelectItem selectItem2= new SelectItem();
            selectItem.setLabel("Mac");
            selectItem.setValue("03");
            monthList.add(selectItem2);
            SelectItem selectItem3= new SelectItem();
            selectItem.setLabel("April");
            selectItem.setValue("04");
            monthList.add(selectItem3);
            SelectItem selectItem4= new SelectItem();
            selectItem.setLabel("Mei");
            selectItem.setValue("05");
            monthList.add(selectItem4);
            SelectItem selectItem5= new SelectItem();
            selectItem.setLabel("Jun");
            selectItem.setValue("06");
            monthList.add(selectItem5);
            SelectItem selectItem6= new SelectItem();
            selectItem.setLabel("Julai");
            selectItem.setValue("07");
            monthList.add(selectItem6);
            SelectItem selectItem7= new SelectItem();
            selectItem.setLabel("Ogos");
            selectItem.setValue("08");
            monthList.add(selectItem7);
            SelectItem selectItem8= new SelectItem();
            selectItem.setLabel("September");
            selectItem.setValue("09");
            monthList.add(selectItem8);
            SelectItem selectItem9= new SelectItem();
            selectItem.setLabel("Oktober");
            selectItem.setValue("10");
            monthList.add(selectItem9);
            SelectItem selectItem10= new SelectItem();
            selectItem.setLabel("November");
            selectItem.setValue("11");
            monthList.add(selectItem10);
            SelectItem selectItem11= new SelectItem();
            selectItem.setLabel("Disember");
            selectItem.setValue("12");
            monthList.add(selectItem11);
           
            
			
		}
        return monthList;
	}
	
	/**
	 * Generate year list.
	 *
	 * @return the list
	 */
	public List<String> generateYearList(){
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		
		Date currentDate = new Date();
		Integer year = null;
		String yearFrom=outputDateFormat.format(currentDate).substring(6, 10);
		if(yearFrom!=null){
			year = Integer.parseInt(yearFrom);
		}
		yearList = new ArrayList<String>();
		if(year!=null){
		 for (int i = year-7; i <= year; i++) {
			 yearList.add(Integer.toString(i));
		 }
		}
		 return yearList; 
	}
}
