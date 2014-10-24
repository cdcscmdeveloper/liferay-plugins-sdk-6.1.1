package my.ep.web.backingbean.report;


import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
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
	ReportsGenerateBean rptGenerateBean = new ReportsGenerateBean();		
	private Date fromDate;
	private Date toDate;	
	private String scQuotationId;
	private String scQuotationNo;	
	private String scPaymentId;
	private String constructedParamString;
	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public ReportsGenerateBean getRptGenerateBean() {
		return rptGenerateBean;
	}
	public void setRptGenerateBean(ReportsGenerateBean rptGenerateBean) {
		this.rptGenerateBean = rptGenerateBean;
	}
	public String getConstructedParamString() {
		return constructedParamString;
	}
	public void setConstructedParamString(String constructedParamString) {
		this.constructedParamString = constructedParamString;
	}
	
	public String getScQuotationId() {
		return scQuotationId;
	}
	public void setScQuotationId(String scQuotationId) {
		this.scQuotationId = scQuotationId;
	}
	public String getScQuotationNo() {
		return scQuotationNo;
	}
	public void setScQuotationNo(String scQuotationNo) {
		this.scQuotationNo = scQuotationNo;
	}
	public String getScPaymentId() {
		return scPaymentId;
	}
	public void setScPaymentId(String scPaymentId) {
		this.scPaymentId = scPaymentId;
	}

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
	            break;
		}		
	}
	/*
	 *validate quotNo 
	 */
	public void quotationNumberValidation() {
		if((this.getScQuotationNo()!=null)&&(this.getScQuotationNo()!=ReportConst.EMPTY_VAL)){
			specialCharValidation();			
		}		
	}
	public void specialCharValidation() {
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
	    Matcher match = pattern.matcher(this.getScQuotationNo());
	    if(match.matches()){
	    	getQuotationNumber();
	    }else{
	    	this.setScQuotationNo(null);
	    	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NUMERIC_CHAR_VALID_ONLY));
	    }	  
	}
	/*
	 *retrieve qtid based on qtno 
	 */
	public void getQuotationNumber() {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		log.info("QtNo="+this.getScQuotationNo()); 
		tenderReqDTO.setQtTndrNo(this.getScQuotationNo());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQtByQtNumber(tenderReqDTO);
		ScQt scqt = resDTO.getQtDetail();	
		if(scqt!=null){
			if(scqt.getQtId()!=null){
				log.info("QtId="+scqt.getQtId());
				this.setScQuotationId(scqt.getQtId().toString());			
			}else {
				this.setScQuotationNo(null);
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
			}
		}else{
			this.setScQuotationNo(null);
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
		}
	}
	
}
