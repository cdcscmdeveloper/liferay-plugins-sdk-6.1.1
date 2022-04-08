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
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.scretry.IScRetrySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.scretry.ScRetryReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.scretry.ScDisruptionLog;
import my.ep.persistence.entity.sc.scretry.ScQtDisruptionLog;
import my.ep.persistence.entity.sc.scretry.ScSQDisruptionLog;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

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
	
	private IPagingDataModel<ScDisruptionLog> retryQTDataModel = new RetryQTPagingDataModel();
	private String reason;
	private String docType;
	private String docNo;
	private Integer disruptionStage;
	private ScDisruptionLog selectedRetryQT;
	private List<ScDisruptionLog> selectedRetryQTs;
	
	public QTRetryBackingBean() {
	}
	
	/**
	 * Reload retry QTs Action.
	 */
	public void reloadRetryQTAction() {
		this.retryQTDataModel.reloadData();
	}
	
	/**
	 * Add new Sc Retry Action
	 */
	public void addScRetryAction(){ 
		log.info("addScRetryAction - start");
		
		if (StringUtils.isNotBlank(this.docType) &&
				StringUtils.isNotBlank(this.docNo) && 
				this.disruptionStage != null &&
				StringUtils.isNotBlank(this.reason)) {
			ScDisruptionLog scDisruptionLog = null;
			if (ParameterConst.DOCTYPE_QUOTATION.equals(this.docType)) {
				IQTDetailSO qtDetailSO;
				try {
					qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
					
					QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
					reqDTO.setQtTndrNo(this.docNo);
					ScQt scQt = qtDetailSO.retriveQtByQtNumber(reqDTO).getQtDetail();
					if (scQt != null) {
						scDisruptionLog = new ScQtDisruptionLog();
						((ScQtDisruptionLog)scDisruptionLog).setScQt(scQt);
					} else {
						throw new EpException(MessageConst.ERROR_100004,
								new String[] { 
									ResourceBundleUtils.getMessage(
										"lbl.sc.document.no", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				} finally {
					qtDetailSO = null;
				}
				
			} else if (ParameterConst.DOCTYPE_SIMPLE_QUOTE.equals(this.docType)) {
				IQuoteSO quoteSO;
				try {
					quoteSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					
					QuoteReqDTO reqDTO = new QuoteReqDTO();
					reqDTO.setQuoteNo(this.docNo);
					ScQuote quote = quoteSO.getQuoteDtlByQuoteNo(reqDTO).getQuoteBidDtl();
					if (quote != null) {
						scDisruptionLog = new ScSQDisruptionLog();
						((ScSQDisruptionLog)scDisruptionLog).setScQuote(quote);
					} else {
						throw new EpException(MessageConst.ERROR_100004,
								new String[] { 
									ResourceBundleUtils.getMessage(
										"lbl.sc.document.no", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				} finally {
					quoteSO = null;
				}
			}
			
			if (scDisruptionLog != null) {
				IScRetrySO scRetrySO;
				try {
					scRetrySO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IScRetrySO.class);
					
					scDisruptionLog.setDisruptionStage(this.disruptionStage);
					scDisruptionLog.setErrorMsg(this.reason);
					super.setUserDetails(scDisruptionLog);
					
					log.info(new StringBuilder().append("docType:").append(scDisruptionLog.getDocType())
							.append(", docNo:").append(scDisruptionLog.getDocNo())
							.append(", disruptionStage:").append(scDisruptionLog.getDisruptionStage())
							.append(", errorMsg:").append(scDisruptionLog.getErrorMsg())
							.toString());
					
					ScRetryReqDTO reqDTO = new ScRetryReqDTO();
					reqDTO.setScDisruptionLog(scDisruptionLog);
					scRetrySO.saveScDisruption(reqDTO);	
					
					reloadRetryQTAction();
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils.getMessage(
											MessageConst.INFO_700004,
											new String[] { 
													getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, scDisruptionLog.getDocType()),
													scDisruptionLog.getDocNo() },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));	
				} finally {
					scRetrySO = null;
					this.docType = null;
					this.docNo = null;
					this.disruptionStage = null;
					this.reason = null;
				}
			}
		}
		
		RequestContext.getCurrentInstance().execute("addDisruptionPopUp.hide()");
		
		log.info("addScRetryAction - end");
	}
	
	/**
	 * Delete Sc Retry Action
	 */
	public void deleteScRetryAction(){ 
		log.info("deleteScRetryAction - start");
		if (CollectionUtils.isNotEmpty(this.selectedRetryQTs)) {
			List<ScDisruptionLog> selectedRetryQTs = new ArrayList<ScDisruptionLog>(this.selectedRetryQTs);
			IScRetrySO scRetrySO;
			try {
				scRetrySO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IScRetrySO.class);
				
				for (ScDisruptionLog scDisruptionLog : selectedRetryQTs) {
					scDisruptionLog.setSuccess(true);
					scDisruptionLog.setErrorMsg(new StringBuilder()
						.append(scDisruptionLog.getErrorMsg())
						.append("\r\nInactive Reason:")
						.append(this.reason)
						.toString());
					super.setUserDetails(scDisruptionLog);
					
					log.info(new StringBuilder().append("disruptionLogId:").append(scDisruptionLog.getDisruptionLogId())
							.append(", docType:").append(scDisruptionLog.getDocType())
							.append(", docNo:").append(scDisruptionLog.getDocNo())
							.append(", disruptionStage:").append(scDisruptionLog.getDisruptionStage())
							.append(", errorMsg:").append(scDisruptionLog.getErrorMsg())
							.toString());
					
					ScRetryReqDTO reqDTO = new ScRetryReqDTO();
					reqDTO.setScDisruptionLog(scDisruptionLog);
					scRetrySO.saveScDisruption(reqDTO);
					
					this.selectedRetryQTs.remove(scDisruptionLog);
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils.getMessage(
											MessageConst.INFO_721205,
											new String[] { 
													getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, scDisruptionLog.getDocType()),
													scDisruptionLog.getDocNo() },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));	
				}
				
				reloadRetryQTAction();
			} finally {
				scRetrySO = null;
				this.reason = null;
			}
		}
		
		RequestContext.getCurrentInstance().execute("deleteDisruptionPopUp.hide()");
		
		log.info("deleteScRetryAction - end");
	}
	
	/**
	 * Retry QT Action.
	 */
	public void submitQTRetryAction() {
		log.info("submitQTRetryAction - start");
		
		if (CollectionUtils.isNotEmpty(this.selectedRetryQTs)) {
			List<ScDisruptionLog> selectedRetryQTs = new ArrayList<ScDisruptionLog>(this.selectedRetryQTs);
			
			IScRetrySO qtRetrySO;
			try {
				qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IScRetrySO.class);
				
				ScRetryReqDTO reqDTO = new ScRetryReqDTO();
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				for (ScDisruptionLog selectedRetryQT : selectedRetryQTs) {
					reqDTO.setScDisruptionLog(selectedRetryQT);
					reqDTO.setDisruptionStage(selectedRetryQT.getDisruptionStage());
					String errorMessage = qtRetrySO.retryScService(reqDTO)
							.getErrorMessage();
					
					if (StringUtils.isNotBlank(errorMessage)) {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										null,
										getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, selectedRetryQT.getDocType()) + " " +
										selectedRetryQT.getDocNo() + ": " + 
										errorMessage));	
					} else {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils.getMessage(
												MessageConst.INFO_700005,
												new String[] { 
														getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, selectedRetryQT.getDocType()),
														selectedRetryQT.getDocNo() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));	
					}
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
	 * Gets the doc types dropdown values for sc retry.
	 *
	 * @return the doc types dropdown values for sc retry
	 */
	public SelectItem[] getScRetryDocTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> docTypes = new ArrayList<String>();	
		docTypes.add(ParameterConst.DOCTYPE_QUOTATION);
		docTypes.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), docTypes));
		return getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
	}

	/**
	 * Get the RetryQTDataModel.
	 * @return the retryQTDataModel
	 */
	public IPagingDataModel<ScDisruptionLog> getRetryQTDataModel() {
		return retryQTDataModel;
	}

	/**
	 * Set the RetryQTDataModel.
	 * @param retryQTDataModel the retryQTDataModel to set
	 */
	public void setRetryQTDataModel(IPagingDataModel<ScDisruptionLog> retryQTDataModel) {
		this.retryQTDataModel = retryQTDataModel;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the disruptionStage
	 */
	public Integer getDisruptionStage() {
		return disruptionStage;
	}

	/**
	 * @param disruptionStage the disruptionStage to set
	 */
	public void setDisruptionStage(Integer disruptionStage) {
		this.disruptionStage = disruptionStage;
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
	public ScDisruptionLog getSelectedRetryQT() {
		return selectedRetryQT;
	}

	/**
	 *  Set the SelectedRetryQT.
	 * @param selectedRetryQT the selectedRetryQT to set
	 */
	public void setSelectedRetryQT(ScDisruptionLog selectedRetryQT) {
		this.selectedRetryQT = selectedRetryQT;
	}

	/**
	 *  Get the SelectedRetryQT.
	 * @return the selectedRetryQTs
	 */
	public List<ScDisruptionLog> getSelectedRetryQTs() {
		return selectedRetryQTs;
	}

	/**
	 *  Set the SelectedRetryQT.
	 * @param selectedRetryQTs the selectedRetryQTs to set
	 */
	public void setSelectedRetryQTs(List<ScDisruptionLog> selectedRetryQTs) {
		this.selectedRetryQTs = selectedRetryQTs;
	}

	/**
	 * Retry quotation/tender list paging class
	 */
	private class RetryQTPagingDataModel extends PagingDataModel<ScDisruptionLog> {

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
		public ISearchResultBean<ScDisruptionLog> pageChangeListener(ISearchBean<ScDisruptionLog> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IScRetrySO scRetrySO = null;
			try {
				scRetrySO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScRetrySO.class);

				ScRetryReqDTO reqDTO = new ScRetryReqDTO();
				reqDTO.setScRetrySearchBean(searchBean);
				return scRetrySO
						.retrieveScRetryList(reqDTO)
						.getScRetryResultBean();

			} finally {
				scRetrySO = null;
			}
		}
	}

}
