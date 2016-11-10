/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.marketresearch;


import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.MarketResearchSearchBean;
import my.ep.web.managedbean.sc.marketresearch.MarketResearchManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;
import my.ep.web.vo.MarketResearchVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;




/**
 * The backing bean for Access Market Research
 * 
 * @author Ken
 * @since 21.12.2015
 */
@ManagedBean(name = "marketResearchBackingBean")
@ViewScoped
public class MarketResearchBackingBean extends
	SourcingBaseBackingBean<MarketResearchManagedBean> implements Serializable{

	/**
	 * The serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger
			.getLogger(MarketResearchBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_SEARCH_MAIN = "/xhtml/sc/marketresearch/marketResearchSearchMain.xhtml";
	public static final String NAV_TO_DETAILS_MAIN = "/xhtml/sc/marketresearch/marketResearchMain.xhtml";
	// Paging for listing
	private IPagingDataModel<ScLoa> marketResearchDataModel = new MarketResearchPagingDataModel();
	
	public MarketResearchBackingBean(){
		
	}
	
	public void preLoadingMarketResearch(){
		log.info("preLoadingMarketResearch");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH_MAIN);
	}
	
	
	// pagination for market research result list
	private class MarketResearchPagingDataModel extends PagingDataModel<ScLoa> {
		private static final long serialVersionUID = 1L;

		public MarketResearchPagingDataModel() {
			super("marketdetailsId");
		}

		@Override
		protected ISearchResultBean<ScLoa> pageChangeListener(
					ISearchBean<ScLoa> searchBean) {
			log.info("pageChangeListener");
			if(getManagedBean().isSearch()){
				IQTAwardSO qtAwardSO = null;
				try {
					AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
					MarketResearchSearchBean mrSearchBean = getManagedBean().getMarketResearchSearchBean();
					if(StringUtils.isNotBlank(mrSearchBean.getProcurementTitle())){
						reqDTO.setProcurementTitle(mrSearchBean.getProcurementTitle());
					}
					
					if(StringUtils.isNotBlank(mrSearchBean.getItemName())){
						reqDTO.setItemName(mrSearchBean.getItemName());
					}
					
					if(StringUtils.isNotBlank(mrSearchBean.getSpec())){
						reqDTO.setSpec(mrSearchBean.getSpec());
					}
					
					if(StringUtils.isNotBlank(mrSearchBean.getLoaNo())){
						reqDTO.setLoaNo(mrSearchBean.getLoaNo());
					}
					
					if(mrSearchBean.getLoaAckDateFrom() != null){
						reqDTO.setLoaAckDateFrom(mrSearchBean.getLoaAckDateFrom());
					}
					
					if(mrSearchBean.getLoaAckDateTo() != null){
						reqDTO.setLoaAckDateTo(mrSearchBean.getLoaAckDateTo());
					}
					
					reqDTO.setScLoaSearchBean(searchBean);
					qtAwardSO = BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
									IQTAwardSO.class);
					AwardSupplierResDTO resDTO = qtAwardSO
							.searchMarketResearchLOA(reqDTO);
					if (resDTO != null) {
						return resDTO.getLoaResultBean();
					}
				}catch(Exception e){
					throw e;
				}finally{
					qtAwardSO = null;
				}	
			}
			return null;
		}
	}

	/**
	 * @return the marketResearchDataModel
	 */
	public IPagingDataModel<ScLoa> getMarketResearchDataModel() {
		return marketResearchDataModel;
	}

	/**
	 * @param marketResearchDataModel the marketResearchDataModel to set
	 */
	public void setMarketResearchDataModel(
			IPagingDataModel<ScLoa> marketResearchDataModel) {
		this.marketResearchDataModel = marketResearchDataModel;
	}
	
	public void searchMarketResearch(){
		log.info("searchMarketResearch");
		getManagedBean().setSearch(true);
		MarketResearchSearchBean mrSearchBean = getManagedBean().getMarketResearchSearchBean();	
		boolean searchValue=checkSearchValues(mrSearchBean);
		if(!searchValue){
			throw new EpException(MessageConst.ERROR_760438);
			}
		if(mrSearchBean.getLoaAckDateFrom() != null && mrSearchBean.getLoaAckDateTo() != null){
			if (DateUtils.isDateGreater(mrSearchBean.getLoaAckDateFrom() ,
					mrSearchBean.getLoaAckDateTo())) {
				throw new EpException(MessageConst.ERROR_760439);
			}
		}
		getMarketResearchDataModel().reloadData();
	}
	
	public void resetMarketResearch(){
		log.info("resetMarketResearch");
		getManagedBean().setMarketResearchSearchBean(null);
		getMarketResearchDataModel().resetData();
	}
	
	public void marketResearchDetailsChange(Integer activeIndex) {
		this.getManagedBean().setActiveIndex(activeIndex);
	}
	
	/**
	 * Method to return the maintance page.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @return maintanacepage
	 */
	public String redirectToMarketResearchMaintenance(Long id,Long loaId,String procurementMethod) {
		getManagedBean().setActiveIndex(0);
		getManagedBean().setMarketResearchVO(null);
		getManagedBean().setProcurementMethod(procurementMethod);
		if(ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(procurementMethod) 
				|| ParameterConst.PROCUREMENT_MODE_TENDER.equals(procurementMethod) 
				||ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equals(procurementMethod)){
			initQt(id, loaId);
		}else if(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE.equals(procurementMethod)){
			initSimpleQuote(id,loaId);
		}else{
			throw new EpException("Invalid Procurement Mode.");
		}
		
		return NAV_TO_DETAILS_MAIN;
	}
	

	public String backToSearch() {
		return NAV_TO_SEARCH_MAIN;
	}
	
	private void initQt(Long qtId,Long loaId){
		log.info("initQt");
//		IQTDetailSO detailSO = null;
		MarketResearchVO vo = null;
		try {
//			detailSO = BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setMaintanceFlow(true);
			reqDTO.setQtId(qtId);
//			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);	
			ScLoa loa = retrieveLoaByLoaId(loaId,true);
			if (loa != null) {
				ScQt qt = loa.getScQt();
				vo = new MarketResearchVO();
				vo.setLoaNo(loa.getLoaNo());
				vo.setIsPanel(qt.isPanel());
				if(loa.isPanel()){
					vo.setCircularNo(loa.getCtCircularVer() != null ? loa.getCtCircularVer().getCircularNo() : null);
					vo.setItemGroupName(loa.getCtPanelItemGroup() != null ? loa.getCtPanelItemGroup().getGroupName() : null);
				}
				vo.setProcurementTypeCatId(loa.getProcurementTypeCatId());	
				vo.setContractDuration(loa.getContractDuration());
				vo.setContractType(loa.getContractType());
				
				vo.setZonal(loa.getScLoiLoa().isZonal());
				vo.setFulfilmentType(loa.getScLoiLoa().getFulfilmentTypeCode());
				vo.setProcurementTitle(loa.getScLoiLoa().getTitle());
				vo.setProcurementMode(loa.getProcurementMode());
				
				vo.setOpenFor(qt.getOpenFor());
				vo.setPmProcurementCategory(qt.getPmProcurementCategory());
				vo.setEvaluationType(qt.getEvaluationType());
				vo.setEstimatedAmount(qt.getEstimatedAmount());
				vo.setSuppFinalizationMethodCode(loa.getFinalizationMethod());
				
				vo.setLoaAmt(loa.getLoaAmt());
				vo.setGstAmt(loa.getGstAmt());
				vo.setAckDate(loa.getAckDate());
				// overall ct amount or loa amt
				vo.setOverallContractAmt(overallCtAmount(loa.getLoaAmt(), loa.getGstAmt()));
				
				getManagedBean().setQt(true);
				getManagedBean().setMarketResearchVO(vo);
				getManagedBean().setScLoa(loa);
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally {			
//			detailSO = null;
		}
	}
	
	private void initSimpleQuote(Long quoteId, Long loaId){
		log.info("initSimpleQuote");
//		IQuoteSO quoteSO = null;
		MarketResearchVO vo = null;
		try{
//			quoteSO =  BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
//			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
//			
//			quoteReqDTO.setQuoteId(quoteId);
//			quoteReqDTO.setFetchSubEntity(true);
//			QuoteResDTO quoteResDTO = quoteSO.getQuoteDtl((quoteReqDTO));
			ScLoa loa = retrieveLoaByLoaId(loaId,false);
//			if(quoteResDTO.getQuoteBidDtl() != null && loa != null){
//				ScQuote quote = quoteResDTO.getQuoteBidDtl();
			if(loa != null){
				vo = new MarketResearchVO();
				vo.setLoaNo(loa.getLoaNo());
				vo.setProcurementTypeCatId(loa.getProcurementTypeCatId());	
				vo.setContractDuration(loa.getContractDuration());
				vo.setContractType(loa.getContractType());
				
				vo.setZonal(loa.getScLoiLoa().isZonal());
				vo.setFulfilmentType(loa.getScLoiLoa().getFulfilmentTypeCode());
				vo.setProcurementTitle(loa.getScLoiLoa().getTitle());
				vo.setSuppFinalizationMethodCode(loa.getFinalizationMethod());
				
//				vo.setOpenFor(quote.getOpenFor());
//				vo.setPmProcurementCategory(quote.getPmProcurementCategory());
//				vo.setEvaluationType(quote.getEvaluationType());
//				vo.setEstimatedAmount(quote.getEstimatedAmount());
//				
				vo.setProcurementMode(loa.getProcurementMode());
				
				vo.setLoaAmt(loa.getLoaAmt());
				vo.setGstAmt(loa.getGstAmt());
				vo.setAckDate(loa.getAckDate());
				// overall ct amount or loa amt
				vo.setOverallContractAmt(overallCtAmount(loa.getLoaAmt(), loa.getGstAmt()));
				
				getManagedBean().setQt(false);
				getManagedBean().setMarketResearchVO(vo);
				getManagedBean().setScLoa(loa);
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally{
//			quoteSO = null;
		}
		
	}
	
	private ScLoa retrieveLoaByLoaId(Long loaId,boolean fetchQt){
		IQTAwardSO qtAwardSO = null;
		ScLoa loa = null;
		try {
			qtAwardSO =  BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoaId(loaId);
			awardSupplierReqDTO.setFetchQt(fetchQt);
			AwardSupplierResDTO awardSupplierResDTO = qtAwardSO.retrieveLOADtlForMarketResearchByLoaId(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoa() != null) {
				loa = awardSupplierResDTO.getScLoa();
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally {
			qtAwardSO = null;
		}
		return loa;
	}
	
	public BigDecimal calcSubTotalPrice(ScLoiLoaItem scLoiLoaItem) {
		BigDecimal total = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
				scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());

		return total;
	}
	
	public void downloadSpecReport() throws IOException {
		log.info("downloadSpecReport");
		ScLoa loa= getManagedBean().getScLoa();
		IQTDocumentSO qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
    	QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
    	
    	String docCategory = null;
    	if(ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(getManagedBean().getProcurementMethod()) 
				|| ParameterConst.PROCUREMENT_MODE_TENDER.equals(getManagedBean().getProcurementMethod()) ){
    		docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER;
		}else if(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equals(getManagedBean().getProcurementMethod())){
			docCategory = ParameterConst.DOCUMENT_CATEGORY_DIRECT_LOA_FOR_QUOTATION_TENDER;
		}
    
		reqDTO.setDocCategory(docCategory);
		reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_SPECIFICATION);
		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(new ScLoa());
		scLoaReport.getScLoa().setLoaId(loa.getLoaId());
		reqDTO.setScReport(scLoaReport);

		QTDocumentResDTO resDTO = qtDocumentSO.retriveScQtReportByDefKey(reqDTO);
		scLoaReport = (ScLoaReport)resDTO.getScReport();
		
		if(scLoaReport != null && scLoaReport.getScLoaReportAttachment() != null){
			downloadFile(scLoaReport.getScLoaReportAttachment());
		}else{
			throw new EpException("Specification Attachment is not is not found.");
		}
	}
	
	
	private ScQtFinalization retrieveSupplierFinalization(ScQt qt) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		ScQtFinalization scQtFinalization = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(qt);
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
												.retrieveSupplierFinalizationDtls(reqDTO);
			scQtFinalization = resDTO.getScQtFinalization();
			
		} finally {
			qtSuppFinalizationSO = null;
		}
		return scQtFinalization;
	}
	
	private boolean checkSearchValues(MarketResearchSearchBean mrSearchBean){
		
		if(StringUtils.isNotBlank(mrSearchBean.getProcurementTitle())){
			return Boolean.TRUE;
			
		}
		
		if(StringUtils.isNotBlank(mrSearchBean.getItemName())){
			return Boolean.TRUE;
			
		}
		
		if(StringUtils.isNotBlank(mrSearchBean.getSpec())){
			return Boolean.TRUE;
			
		}
		
		if(StringUtils.isNotBlank(mrSearchBean.getLoaNo())){
			return Boolean.TRUE;
			
		}
		
		
	
		if(mrSearchBean.getLoaAckDateFrom() != null){
			return Boolean.TRUE;
			
		}
		
		if(mrSearchBean.getLoaAckDateTo() != null){
			return Boolean.TRUE;
			
		}
		
		return Boolean.FALSE;
	}
}
