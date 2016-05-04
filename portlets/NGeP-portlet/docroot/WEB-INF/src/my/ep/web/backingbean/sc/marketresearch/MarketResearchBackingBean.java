/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.marketresearch;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.MarketResearchSearchBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.marketresearch.MarketResearchManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;
import my.ep.web.vo.MarketResearchVO;




/**
 * The backing bean for Access Market Research
 * 
 * @author Ken
 * @since 21.12.2015
 */
@ManagedBean(name = "marketResearchBackingBean")
@ViewScoped
public class MarketResearchBackingBean extends
	SourcingBaseBackingBean<MarketResearchManagedBean> {

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
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH_MAIN);
	}
	
	public List<PmParameterDesc> getProcModeCategories(){
		return ParameterHelper
				.getInstance()
				.getParameterDescList(ParameterTypeConst.SC_PROCUREMENT_MODE);
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
					
					if(StringUtils.isNotBlank(mrSearchBean.getSelectedProcurementMethod())){
						reqDTO.setProcurementMethod(mrSearchBean.getSelectedProcurementMethod());
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
		getManagedBean().setSearch(true);
		MarketResearchSearchBean mrSearchBean = getManagedBean().getMarketResearchSearchBean();		
		if(mrSearchBean.getLoaAckDateFrom() != null && mrSearchBean.getLoaAckDateTo() != null){
			if (DateUtils.isDateGreater(mrSearchBean.getLoaAckDateFrom() ,
					mrSearchBean.getLoaAckDateTo())) {
				throw new EpException("Loa From Date is greater than Loa To Date");
			}
		}
		getMarketResearchDataModel().reloadData();
	}
	
	public void resetMarketResearch(){
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
		if(ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(procurementMethod) 
				|| ParameterConst.PROCUREMENT_MODE_TENDER.equals(procurementMethod) 
				||ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equals(procurementMethod)){
			initQt(id, loaId);
		}else if(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE.equals(procurementMethod)){
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
		IQTDetailSO detailSO = null;
		MarketResearchVO vo = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setMaintanceFlow(true);
			reqDTO.setQtId(qtId);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);	
			ScLoa loa = retrieveLoaByLoaId(loaId);
			if (resDTO.getQtDetail() != null && loa != null) {
				ScQt qt = resDTO.getQtDetail();
				vo = new MarketResearchVO();
				vo.setIsPanel(qt.isPanel());
				if(qt.isPanel()){
					vo.setCircularNo(qt.getCircularVer() != null ? qt.getCircularVer().getCircularNo() : null);
					vo.setItemGroupName(qt.getItemGroup() != null ? qt.getItemGroup().getGroupName() : null);
				}
				vo.setProcurementTitle(qt.getQtTitle());
				vo.setProcurementMode(qt.getProcurementMode());
				vo.setEstimatedAmount(qt.getEstimatedAmount());
				vo.setOpenFor(qt.getOpenFor());
				vo.setZonal(qt.isZonal());
				vo.setProcurementTypeCatId(qt.getProcurementTypeCatId());
				vo.setPmProcurementCategory(qt.getPmProcurementCategory());
				vo.setEvaluationType(qt.getEvaluationType());
				vo.setContractType(qt.getContractType());
				vo.setFulfilmentType(qt.getFulfilmentType());
				vo.setContractDuration(qt.getContractDuration());
				vo.setLoaAmt(loa.getLoaAmt());
				vo.setGstAmt(loa.getGstAmt());
				// overall ct amount or loa amt
				vo.setOverallContractAmt(overallCtAmount(loa.getLoaAmt(), loa.getGstAmt()));
				vo.setAckDate(loa.getAckDate());
				
				getManagedBean().setMarketResearchVO(vo);
				getManagedBean().setScLoa(loa);
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally {			
			detailSO = null;
		}
	}
	
	private void initSimpleQuote(Long quoteId, Long loaId){
		IQuoteSO quoteSO = null;
		MarketResearchVO vo = null;
		try{
			quoteSO =  BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
			
			quoteReqDTO.setQuoteId(quoteId);
			quoteReqDTO.setFetchSubEntity(true);
			QuoteResDTO quoteResDTO = quoteSO.getQuoteDtl((quoteReqDTO));
			ScLoa loa = retrieveLoaByLoaId(loaId);
			if(quoteResDTO.getQuoteBidDtl() != null && loa != null){
				ScQuote quote = quoteResDTO.getQuoteBidDtl();
				vo = new MarketResearchVO();
				vo.setIsPanel(quote.isPanel());
				vo.setProcurementTitle(quote.getTitle());
				vo.setProcurementMode(getParameterByTypeAndCode(ParameterTypeConst.SC_PROCUREMENT_MODE,
						ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE));
				//TODO //vo.setOpenFor(quote.getOpenFor());
				vo.setZonal(quote.isZonal());
				vo.setProcurementTypeCatId(quote.getProcurementTypeCategory());
				//vo.setPmProcurementCategory(quote.getPmProcurementCategory());
//				vo.setEvaluationType(quote.getEvaluationType());
//				vo.setContractType(quote.getContractType());
//				vo.setFulfilmentType(quote.getFulfilmentType());
//				vo.setContractDuration(quote.getContractDuration());
				vo.setLoaAmt(loa.getLoaAmt());
				vo.setGstAmt(loa.getGstAmt());
				// overall ct amount or loa amt
				vo.setOverallContractAmt(overallCtAmount(loa.getLoaAmt(), loa.getGstAmt()));
				vo.setAckDate(loa.getAckDate());
				getManagedBean().setMarketResearchVO(vo);
				getManagedBean().setScLoa(loa);
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally{
			quoteSO = null;
		}
		
	}
	
	private ScLoa retrieveLoaByLoaId(Long loaId){
		IQTAwardSO qtAwardSO = null;
		ScLoa loa = null;
		try {
			qtAwardSO =  BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoaId(loaId);
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
}
