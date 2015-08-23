package my.ep.web.backingbean.soa.batchretry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.soa.batchretry.ISOABatchRetrySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.soa.batchretry.SOABatchRetryReqDTO;
import my.ep.dto.soa.batchretry.SOABatchRetryResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbBatchRetryDtl;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.soa.batchretry.SOABatchRetryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "soaBatchRetryBackingBean")
@ViewScoped
public class SOABatchRetryBackingBean extends
		BaseBackingBean<SOABatchRetryManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(SOABatchRetryBackingBean.class);

	private IPagingDataModel<OsbBatchRetryDtl> batchRetryDataModel = new SOABatchRetryDataModel();

	public SOABatchRetryBackingBean() {
	}

	/**
	 * template spec list paging class
	 * 
	 * @author Jimmy
	 */
	private class SOABatchRetryDataModel extends PagingDataModel<OsbBatchRetryDtl> {

		private static final long serialVersionUID = 1L;

		public SOABatchRetryDataModel() {
			super("batchRetryTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<OsbBatchRetryDtl> pageChangeListener(
				ISearchBean<OsbBatchRetryDtl> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			ISOABatchRetrySO logSO;
			try {
				logSO = (ISOABatchRetrySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SOABATCH,
								ISOABatchRetrySO.class);
				
				SOABatchRetryReqDTO reqDTO = new SOABatchRetryReqDTO();
				SOABatchRetryResDTO resDTO = logSO.searchSOABatchRetryList(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				logSO = null;
			}

			return null;
		}
	}
	
	/**
	 * Functional methods.
	 */

	public void refreshBatchRetryList() {
		debugLogger("refreshBatchRetryList");
		resetAndReloadData();
	}

	public void submitBatchRetryList() {
		submitOrRemoveBatchRetryList(true);
	}

	public void removeBatchRetryList() {
		submitOrRemoveBatchRetryList(false);
	}
	
	/**
	 * Private methods.
	 */

	private void submitOrRemoveBatchRetryList(boolean isSubmit) {

		if (isSubmit) {
			debugLogger("submitRetryLogList");
		} else {
			debugLogger("removeRetryLogList");
		}

		SOABatchRetryReqDTO reqDTO = new SOABatchRetryReqDTO();

		for (OsbBatchRetryDtl retryDtl : this.getManagedBean()
				.getSelectedRetryLogs()) {
			debugLogger("RetryDtlId : " + retryDtl.getRetryDtlId());
			reqDTO.getOsbBatchRetryDtl().add(retryDtl);
		}

		if (reqDTO.getOsbBatchRetryDtl().isEmpty()) {
				
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							MessageUtils.getMessage(MessageConst.ERROR_990001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
							, null));
			
			return;
		}
		
		ISOABatchRetrySO logSO = (ISOABatchRetrySO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.SOABATCH,
						ISOABatchRetrySO.class);

		SOABatchRetryResDTO resDTO = null;

		try{
		
			if (isSubmit) {
				resDTO = logSO.submitSOABatchRetryList(reqDTO);
			} else {
				resDTO = logSO.removeSOABatchRetryList(reqDTO);
			}

			debugLogger("NotifyResDTO : at least one record isSuccessful[" + resDTO.isSuccessful()+ "]");

			if (resDTO.isSuccessful()) {

				String[] message = null;

				if (resDTO.isAllRemoved()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									MessageUtils.getMessage(MessageConst.ERROR_990002,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())
									, ""));
					
				} else {
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									MessageUtils.getMessage(MessageConst.ERROR_990003,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())
									, ""));
				}
						
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								MessageUtils.getMessage(MessageConst.ERROR_990004,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())
								, ""));
			}
		} catch (Exception ex){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							MessageUtils.getMessage(MessageConst.ERROR_990004,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale()) + ex.getMessage()
							, ""));
		}

		resetAndReloadData();
	}

	private void resetAndReloadData(){
		batchRetryDataModel.resetData();
		batchRetryDataModel.reloadData();
	}
	
	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	
	/**
	 * Setters & getters.
	 */

	public IPagingDataModel<OsbBatchRetryDtl> getBatchRetryDataModel() {
		return batchRetryDataModel;
	}

	public void setRetryLogDataModel(
			IPagingDataModel<OsbBatchRetryDtl> batchRetryDataModel) {
		this.batchRetryDataModel = batchRetryDataModel;
	}
}
