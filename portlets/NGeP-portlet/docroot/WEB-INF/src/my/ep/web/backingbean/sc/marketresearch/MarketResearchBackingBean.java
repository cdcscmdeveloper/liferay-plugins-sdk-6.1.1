/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.marketresearch;


import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.MarketResearchSearchBean;
import my.ep.web.managedbean.sc.marketresearch.MarketResearchManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;




/**
 * The backing bean for Access Market Research
 * 
 * @author Ken
 * @since 21.12.2015
 */
@ManagedBean(name = "marketResearchBackingBean")
@ViewScoped
public class MarketResearchBackingBean extends
		BaseBackingBean<MarketResearchManagedBean> {

	/** The log. */
	private static Logger log = Logger
			.getLogger(MarketResearchBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/sc/marketresearch/marketResearchSearchMain";
	// Paging for listing
	private IPagingDataModel<ScLoa> marketResearchDataModel = new MarketResearchPagingDataModel();
	
	public MarketResearchBackingBean(){
		
	}
	
	public void preLoadingMarketResearch(){
		getManagedBean().setMarketResearchSearchBean(new MarketResearchSearchBean());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
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
			super("marketdetailsId",false);
		}

		@Override
		protected ISearchResultBean<ScLoa> pageChangeListener(
					ISearchBean<ScLoa> searchBean) {
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
		MarketResearchSearchBean mrSearchBean = getManagedBean().getMarketResearchSearchBean();		
		if(mrSearchBean.getLoaAckDateFrom() != null && mrSearchBean.getLoaAckDateTo() != null){
			if (DateUtils.isDateGreater(mrSearchBean.getLoaAckDateFrom() ,
					mrSearchBean.getLoaAckDateTo())) {
				throw new EpException("Loa From Date is greater than Loa To Date");
			}
		}
		getMarketResearchDataModel().reloadData();
	}
	
	
	
	
	

}
