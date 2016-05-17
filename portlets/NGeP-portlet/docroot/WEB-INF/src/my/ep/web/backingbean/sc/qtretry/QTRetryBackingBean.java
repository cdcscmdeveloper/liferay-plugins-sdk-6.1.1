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
import my.ep.dto.sc.quationtender.ProposalReqDTO;
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
			ScQt scQt = qtDisruptionLog.getScQt();
			Long qtId = scQt.getQtId();
			
			switch (disruptionStage) {
				case QTDisruptionConst.QT_PUBLISH_WORK_FLOW_DISRUPTION :
					ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
					proposalReqDTO.setQtId(qtId);
					proposalReqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
					successful = getQTProposalSO().updateRescheduleWorkFlowStatus(proposalReqDTO)
						.isSuccessful();
					if (successful) {
						updateDisruptionSuccess(qtDisruptionLog);
					}
					break;
				
				case QTDisruptionConst.QT_PUBLISH_SUPPLIER_INSERT_DISRUPTION :
					NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					successful = getQTNoticeBoardSO().publishQtAndInsertSuppliers(noticeBoardReqDTO)
						.isSuccessful();
					if (successful) {
						retryQTService(qtDisruptionLog, QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION);
					}
					break;
				case QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					noticeBoardReqDTO.setBpmEndpoint(BpmEndpointConst.SC_PUBLISH_QT_MESSAGEPOINT);
					successful = getQTNoticeBoardSO().updateBpmForPublishAndProposalClose(noticeBoardReqDTO)
							.isSuccessful();
					if (successful) {
						updateDisruptionSuccess(qtDisruptionLog);
					}
					break;
				case QTDisruptionConst.QT_CLOSE_WORK_FLOW_DISRUPTION :
					proposalReqDTO = new ProposalReqDTO();
					proposalReqDTO.setQtId(qtId);
					proposalReqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE);
					successful = getQTProposalSO().updateRescheduleWorkFlowStatus(proposalReqDTO)
						.isSuccessful();
					if (successful) {
						updateDisruptionSuccess(qtDisruptionLog);
					}
					break;
				case QTDisruptionConst.QT_CLOSE_PROPOSAL_AMOUNT_INSERT_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					successful = getQTNoticeBoardSO().closeQtAndSaveProposalAmount(noticeBoardReqDTO)
							.isSuccessful();
					if (successful) {
						retryQTService(qtDisruptionLog, QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION);
					}
					break;
				case QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION :
					noticeBoardReqDTO = new NoticeBoardReqDTO();
					noticeBoardReqDTO.setQtId(qtId);
					noticeBoardReqDTO.setBpmEndpoint(BpmEndpointConst.SC_CLOSE_PROPOSAL_MESSAGEPOINT);
					successful = getQTNoticeBoardSO().updateBpmForPublishAndProposalClose(noticeBoardReqDTO)
							.isSuccessful();
					if (successful) {
						updateDisruptionSuccess(qtDisruptionLog);
					}
					break;
			}
			
			if (!successful) {
				saveQTDisruption(scQt, disruptionStage);
			}
		}
	}
	
	private void updateDisruptionSuccess(ScQtDisruptionLog qtDisruptionLog) {
		IQTRetrySO qtRetrySO;
		try {
			qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTRetrySO.class);
			
			QTRetryReqDTO reqDTO = new QTRetryReqDTO();
			qtDisruptionLog.setSuccess(true);
			reqDTO.setScQtDisruptionLog(qtDisruptionLog);
			
			qtRetrySO.saveQTDisruption(reqDTO);
		} finally {
			qtRetrySO = null;
		}
	}
	
	private void saveQTDisruption(ScQt scQt, Integer disruptionStage) {
		IQTRetrySO qtRetrySO;
		try {
			qtRetrySO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTRetrySO.class);
			
			QTRetryReqDTO reqDTO = new QTRetryReqDTO();
			ScQtDisruptionLog disruptionLog = new ScQtDisruptionLog();
			disruptionLog.setScQt(scQt);
			disruptionLog.setDisruptionStage(disruptionStage);
			disruptionLog.setErrorMsg(getParameterDesc(
					ParameterTypeConst.QT_DISRUPTION_STAGE, 
					disruptionLog.getDisruptionStage()+""));
			super.setUserDetails(disruptionLog);
			reqDTO.setScQtDisruptionLog(disruptionLog);
			
			qtRetrySO.saveQTDisruption(reqDTO);
		} finally {
			qtRetrySO = null;
		}		
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
