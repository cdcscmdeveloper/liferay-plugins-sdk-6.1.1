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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.qtretry.IQTRetrySO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTDisruptionConst;
import my.ep.constants.BpmEndpointConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.qtretry.QTRetryReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.qtretry.ScQtDisruptionLog;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Quotation / Tender Retry Backing Bean
 * 
 * @author Kenny
 * @since 15.05.2016
 */
@ManagedBean(name = "qtRetryBackingBean")
@ViewScoped
public class QTRetryBackingBean extends SourcingBaseBackingBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static Logger log = EpLogger.getLogger(QTRetryBackingBean.class);
	
	private IPagingDataModel<ScQtDisruptionLog> retryQTDataModel = new RetryQTPagingDataModel();
	private String reason;
	private ScQtDisruptionLog selectedRetryQT;
	private List<ScQtDisruptionLog> selectedRetryQTs;
	
	public QTRetryBackingBean() {
	}
	
	/**
	 * Reload retry QTs Action.
	 */
	public void reloadRetryQTAction() {
		this.retryQTDataModel.reloadData();
	}
	
	public void inactiveQTRetryAction(){ 
		if (CollectionUtils.isNotEmpty(this.selectedRetryQTs)) {
			List<ScQtDisruptionLog> selectedRetryQTs = new ArrayList<ScQtDisruptionLog>(this.selectedRetryQTs);
			IQTRetrySO qtRetrySO;
			try {
				qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTRetrySO.class);
				
				for (ScQtDisruptionLog qtDisruptionLog : selectedRetryQTs) {
					qtDisruptionLog.setSuccess(true);
					qtDisruptionLog.setErrorMsg(new StringBuilder()
						.append(qtDisruptionLog.getErrorMsg())
						.append("\r\nInactive Reason:")
						.append(this.reason)
						.toString());
					log.info(new StringBuilder().append("qtDisruptionLogId:")
							.append(qtDisruptionLog.getQtDisruptionLogId())
							.append(", qtNo:" + qtDisruptionLog.getScQt().getQtNo())
							.append(", disruptionStage:" + qtDisruptionLog.getDisruptionStage())
							.append(", errorMsg:" + qtDisruptionLog.getErrorMsg())
							.toString());
					
					QTRetryReqDTO reqDTO = new QTRetryReqDTO();
					reqDTO.setScQtDisruptionLog(qtDisruptionLog);
					qtRetrySO.saveQTDisruption(reqDTO);
				}
				
				reloadRetryQTAction();
			} finally {
				qtRetrySO = null;
				this.reason = null;
			}
		}
	}
	
	/**
	 * Retry QT Action.
	 */
	public void submitQTRetryAction() {
		log.info("submitQTRetryAction - start");
		
		if (CollectionUtils.isNotEmpty(this.selectedRetryQTs)) {
			List<ScQtDisruptionLog> selectedRetryQTs = new ArrayList<ScQtDisruptionLog>(this.selectedRetryQTs);
			
			IQTRetrySO qtRetrySO;
			try {
				qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTRetrySO.class);
				
				QTRetryReqDTO reqDTO = new QTRetryReqDTO();
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				for (ScQtDisruptionLog selectedRetryQT : selectedRetryQTs) {
					reqDTO.setScQtDisruptionLog(selectedRetryQT);
					reqDTO.setDisruptionStage(selectedRetryQT.getDisruptionStage());
					qtRetrySO.retryQTService(reqDTO);
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils.getMessage(
											MessageConst.INFO_700005,
											new String[] { 
													getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,ParameterConst.DOCTYPE_QUOTATION),
													selectedRetryQT.getScQt().getQtNo() },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));	
				}
			} finally {
				qtRetrySO = null;
				this.reason = null;
			}
			
			reloadRetryQTAction();
		}
		
		log.info("submitQTRetryAction - end");
	}

	/**
	 * Get the RetryQTDataModel.
	 * @return the retryQTDataModel
	 */
	public IPagingDataModel<ScQtDisruptionLog> getRetryQTDataModel() {
		return retryQTDataModel;
	}

	/**
	 * Set the RetryQTDataModel.
	 * @param retryQTDataModel the retryQTDataModel to set
	 */
	public void setRetryQTDataModel(IPagingDataModel<ScQtDisruptionLog> retryQTDataModel) {
		this.retryQTDataModel = retryQTDataModel;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Get the SelectedRetryQT.
	 * @return the selectedRetryQT
	 */
	public ScQtDisruptionLog getSelectedRetryQT() {
		return selectedRetryQT;
	}

	/**
	 *  Set the SelectedRetryQT.
	 * @param selectedRetryQT the selectedRetryQT to set
	 */
	public void setSelectedRetryQT(ScQtDisruptionLog selectedRetryQT) {
		this.selectedRetryQT = selectedRetryQT;
	}

	/**
	 *  Get the SelectedRetryQT.
	 * @return the selectedRetryQTs
	 */
	public List<ScQtDisruptionLog> getSelectedRetryQTs() {
		return selectedRetryQTs;
	}

	/**
	 *  Set the SelectedRetryQT.
	 * @param selectedRetryQTs the selectedRetryQTs to set
	 */
	public void setSelectedRetryQTs(List<ScQtDisruptionLog> selectedRetryQTs) {
		this.selectedRetryQTs = selectedRetryQTs;
	}

	/**
	 * Retry quotation/tender list paging class
	 */
	private class RetryQTPagingDataModel extends PagingDataModel<ScQtDisruptionLog> {

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
		public ISearchResultBean<ScQtDisruptionLog> pageChangeListener(ISearchBean<ScQtDisruptionLog> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTRetrySO qtRetrySO = null;
			try {
				qtRetrySO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTRetrySO.class);

				QTRetryReqDTO reqDTO = new QTRetryReqDTO();
				reqDTO.setQtRetrySearchBean(searchBean);
				return qtRetrySO
						.retrieveQTRetryList(reqDTO)
						.getQtRetryResultBean();

			} finally {
				qtRetrySO = null;
			}
		}
	}

}
