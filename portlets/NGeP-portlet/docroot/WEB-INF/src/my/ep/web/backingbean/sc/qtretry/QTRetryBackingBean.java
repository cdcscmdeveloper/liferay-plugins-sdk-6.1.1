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

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sc.qtretry.IQTRetrySO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTDisruptionConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.BpmEndpointConst;
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
public class QTRetryBackingBean extends SourcingBaseBackingBean {
	public static Logger log = EpLogger.getLogger(QTRetryBackingBean.class);
	
	private IPagingDataModel<ScQtDisruptionLog> retryQTDataModel = new RetryQTPagingDataModel();
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
	
	/**
	 * Retry QT Action.
	 */
	public void submitQTRetryAction() {
		log.info("submitQTRetryAction - start");
		
		if (CollectionUtils.isNotEmpty(this.selectedRetryQTs)) {
			List<ScQtDisruptionLog> selectedRetryQTs = new ArrayList<ScQtDisruptionLog>(this.selectedRetryQTs);
			
			for (ScQtDisruptionLog selectedRetryQT : selectedRetryQTs) {
				retryQTService(selectedRetryQT, selectedRetryQT.getDisruptionStage());
				
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
			
			reloadRetryQTAction();
		}
		
		log.info("submitQTRetryAction - end");
	}
	
	private void retryQTService(ScQtDisruptionLog qtDisruptionLog, Integer disruptionStage) {		
		if (disruptionStage != null) {
			boolean successful = false;
			String errorMessage = null;
			ScQt scQt = qtDisruptionLog.getScQt();
			Long qtId = scQt.getQtId();
			
			switch (disruptionStage) {
				case QTDisruptionConst.QT_PUBLISH_WORK_FLOW_DISRUPTION :
					ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
					proposalReqDTO.setQtId(qtId);
					proposalReqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
					ProposalResDTO proposalResDTO = getQTProposalSO()
							.updateRescheduleWorkFlowStatus(proposalReqDTO);
					successful = proposalResDTO.isSuccessful();
					if (successful) {
						updateQTDisruptionSuccess(qtDisruptionLog);
					} else {
						errorMessage = proposalResDTO.getErrorMessage();
					}
					break;
				
				case QTDisruptionConst.QT_PUBLISH_SUPPLIER_INSERT_DISRUPTION :
					NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					NoticeBoardResDTO noticeBoardResDTO = getQTNoticeBoardSO()
							.publishQtAndInsertSuppliers(noticeBoardReqDTO);
					successful = noticeBoardResDTO.isSuccessful();
					if (successful) {
						retryQTService(qtDisruptionLog, QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION);
					} else {
						errorMessage = noticeBoardResDTO.getErrorMessage();
					}
					break;
				case QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					noticeBoardReqDTO.setBpmEndpoint(BpmEndpointConst.SC_PUBLISH_QT_MESSAGEPOINT);
					noticeBoardResDTO = getQTNoticeBoardSO()
							.updateBpmForPublishAndProposalClose(noticeBoardReqDTO);
					successful = noticeBoardResDTO.isSuccessful();
					if (successful) {
						updateQTDisruptionSuccess(qtDisruptionLog);
					} else {
						errorMessage = noticeBoardResDTO.getErrorMessage();
					}
					break;
				case QTDisruptionConst.QT_CLOSE_WORK_FLOW_DISRUPTION :
					proposalReqDTO = new ProposalReqDTO();
					proposalReqDTO.setQtId(qtId);
					proposalReqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE);
					proposalResDTO = getQTProposalSO()
							.updateRescheduleWorkFlowStatus(proposalReqDTO);
					successful = proposalResDTO.isSuccessful();
					if (successful) {
						updateQTDisruptionSuccess(qtDisruptionLog);
					} else {
						errorMessage = proposalResDTO.getErrorMessage();
					}
					break;
				case QTDisruptionConst.QT_CLOSE_PROPOSAL_AMOUNT_INSERT_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					noticeBoardResDTO = getQTNoticeBoardSO()
							.closeQtAndSaveProposalAmount(noticeBoardReqDTO);
					successful = noticeBoardResDTO.isSuccessful();
					if (successful) {
						retryQTService(qtDisruptionLog, QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION);
					} else {
						errorMessage = noticeBoardResDTO.getErrorMessage();
					}
					break;
				case QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					noticeBoardReqDTO.setBpmEndpoint(BpmEndpointConst.SC_CLOSE_PROPOSAL_MESSAGEPOINT);
					noticeBoardResDTO = getQTNoticeBoardSO()
							.updateBpmForPublishAndProposalClose(noticeBoardReqDTO);
					successful = noticeBoardResDTO.isSuccessful();
					if (successful) {
						updateQTDisruptionSuccess(qtDisruptionLog);
					} else {
						errorMessage = noticeBoardResDTO.getErrorMessage();
					}
					break;
			}
			
			if (!successful) {
				if (qtDisruptionLog.getDisruptionStage() != null && 
						!qtDisruptionLog.getDisruptionStage().equals(disruptionStage)) {
					insertQTDisruption(scQt, disruptionStage, errorMessage);
				} else {
					qtDisruptionLog.setErrorMsg(errorMessage);
					qtDisruptionLog.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
					updateQTDisruption(qtDisruptionLog);
				}
			}
		}
	}
	
	private void updateQTDisruptionSuccess(ScQtDisruptionLog qtDisruptionLog) {
		qtDisruptionLog.setSuccess(true);
		updateQTDisruption(qtDisruptionLog);
	}
	
	private void updateQTDisruption(ScQtDisruptionLog qtDisruptionLog) {
		IQTRetrySO qtRetrySO;
		try {
			qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTRetrySO.class);
			
			QTRetryReqDTO reqDTO = new QTRetryReqDTO();
			reqDTO.setScQtDisruptionLog(qtDisruptionLog);
			
			qtRetrySO.saveQTDisruption(reqDTO);
		} finally {
			qtRetrySO = null;
		}
	}
	
	private void insertQTDisruption(ScQt scQt, Integer disruptionStage, String errorMsg) {
		ScQtDisruptionLog disruptionLog = new ScQtDisruptionLog();
		disruptionLog.setScQt(scQt);
		disruptionLog.setDisruptionStage(disruptionStage);
		disruptionLog.setErrorMsg(errorMsg);
		super.setUserDetails(disruptionLog);
		
		updateQTDisruption(disruptionLog);	
	}
	
	private IQTNoticeBoardSO getQTNoticeBoardSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTNoticeBoardSO.class);
	}
	
	private IQTProposalSO getQTProposalSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTProposalSO.class);
	}

	/**
	 * @return the retryQTDataModel
	 */
	public IPagingDataModel<ScQtDisruptionLog> getRetryQTDataModel() {
		return retryQTDataModel;
	}

	/**
	 * @param retryQTDataModel the retryQTDataModel to set
	 */
	public void setRetryQTDataModel(IPagingDataModel<ScQtDisruptionLog> retryQTDataModel) {
		this.retryQTDataModel = retryQTDataModel;
	}

	/**
	 * @return the selectedRetryQT
	 */
	public ScQtDisruptionLog getSelectedRetryQT() {
		return selectedRetryQT;
	}

	/**
	 * @param selectedRetryQT the selectedRetryQT to set
	 */
	public void setSelectedRetryQT(ScQtDisruptionLog selectedRetryQT) {
		this.selectedRetryQT = selectedRetryQT;
	}

	/**
	 * @return the selectedRetryQTs
	 */
	public List<ScQtDisruptionLog> getSelectedRetryQTs() {
		return selectedRetryQTs;
	}

	/**
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
