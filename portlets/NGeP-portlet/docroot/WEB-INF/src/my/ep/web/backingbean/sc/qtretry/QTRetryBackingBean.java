/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.qtretry;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Quotation / Tender Retry Backing Bean
 * 
 * @author Kenny
 * @since 15.05.2016
 */
@ManagedBean(name = "qtRetryBackingBean")
@ViewScoped
public class QTRetryBackingBean extends BaseBackingBean {
	public static Logger log = EpLogger.getLogger(QTRetryBackingBean.class);
	
	private IPagingDataModel<ScQt> retryQTDataModel = new RetryQTPagingDataModel();
	private List<ScQt> selectedRetryQTs;
	
	public QTRetryBackingBean() {
	}
	
	/**
	 * Reload retry QTs Action.
	 */
	public void reloadRetryQTAction() {
		this.retryQTDataModel.reloadData();
	}
	
	/**
	 * Retry QT Action.
	 */
	public void submitQTRetryAction() {
		log.info("submitQTRetryAction - start");
		
		
		log.info("submitQTRetryAction - end");
	}

	/**
	 * @return the retryQTDataModel
	 */
	public IPagingDataModel<ScQt> getRetryQTDataModel() {
		return retryQTDataModel;
	}

	/**
	 * @param retryQTDataModel the retryQTDataModel to set
	 */
	public void setRetryQTDataModel(IPagingDataModel<ScQt> retryQTDataModel) {
		this.retryQTDataModel = retryQTDataModel;
	}

	/**
	 * @return the selectedRetryQTs
	 */
	public List<ScQt> getSelectedRetryQTs() {
		return selectedRetryQTs;
	}

	/**
	 * @param selectedRetryQTs the selectedRetryQTs to set
	 */
	public void setSelectedRetryQTs(List<ScQt> selectedRetryQTs) {
		this.selectedRetryQTs = selectedRetryQTs;
	}

	/**
	 * Retry quotation/tender list paging class
	 */
	private class RetryQTPagingDataModel extends PagingDataModel<ScQt> {

		private static final long serialVersionUID = 1L;

		public RetryQTPagingDataModel() {
			super("qtRetryTbl");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTNoticeBoardSO qtNoticeBoardSO = null;
			try {
				qtNoticeBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTNoticeBoardSO.class);

				NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
				reqDTO.setQtRetrySearchBean(searchBean);
				return qtNoticeBoardSO
						.retrieveQTRetryList(reqDTO)
						.getQtRetryResultBean();

			} finally {
				qtNoticeBoardSO = null;
			}
		}
	}

}
