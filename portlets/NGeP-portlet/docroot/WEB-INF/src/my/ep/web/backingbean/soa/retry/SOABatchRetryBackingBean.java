package my.ep.web.backingbean.soa.retry;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.soa.retry.ISOABatchRetrySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.soa.retry.SOABatchRetryReqDTO;
import my.ep.dto.soa.retry.SOABatchRetryResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbBatchRetryDtl;
import my.ep.util.MessageUtils;
import my.ep.util.SOAUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.soa.retry.SOABatchRetryManagedBean;
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
				
				reqDTO.setISearchBean(searchBean);
				reqDTO.setIbOb(getManagedBean().getIbob());
				reqDTO.setSourceSystem(getManagedBean().getSourceSystem());
				reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
				
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
			debugLogger("submitBatchRetryList");
		} else {
			debugLogger("removeBatchRetryList");
		}

		SOABatchRetryReqDTO reqDTO = new SOABatchRetryReqDTO();

		for (OsbBatchRetryDtl retryDtl : this.getManagedBean()
				.getSelectedBatchRetries()) {
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

				if (resDTO.isAllRemoved()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
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

	public void setServiceBatchRetryDataModel(
			IPagingDataModel<OsbBatchRetryDtl> batchRetryDataModel) {
		this.batchRetryDataModel = batchRetryDataModel;
	}
	
	public SelectItem[] getInboundOutboundDropdown() {
		
		ISOABatchRetrySO batchRetrySO = null;
		SelectItem[] items = null;
		
		try {
			batchRetrySO = (ISOABatchRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SOABATCH,ISOABatchRetrySO.class);
			List<String> inboundOutboundList =  batchRetrySO.getAllInboundOutboundValues();
		    
			if(inboundOutboundList != null){
			 
				items = new SelectItem[inboundOutboundList.size() + 0];
			      
				for(int i = 0; i<inboundOutboundList.size(); i++) {
					String inboundOutboundValue = inboundOutboundList.get(i);
					items[i] = new SelectItem(inboundOutboundValue, inboundOutboundValue);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));

		} finally {
			batchRetrySO = null;
		}
		
	    return items;
	}
	
	public SelectItem[] getSourceSystemStatusDropdown() {
		
		ISOABatchRetrySO batchRetrySO = null;
		SelectItem[] items = null;
		
		try {
			batchRetrySO = (ISOABatchRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SOABATCH,ISOABatchRetrySO.class);
			List<String> sourceSystemList =  batchRetrySO.getAllSourceSystems();
		    
			if(sourceSystemList != null){
			 
				items = new SelectItem[sourceSystemList.size() + 0];
			      
				for(int i = 0; i<sourceSystemList.size(); i++) {
					String sourceSystem = sourceSystemList.get(i);
					items[i] = new SelectItem(sourceSystem, sourceSystem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));

		} finally {
			batchRetrySO = null;
		}
		
	    return items;
	}

	public SelectItem[] getTargetSystemStatusDropdown() {
		
		ISOABatchRetrySO batchRetrySO = null;
		SelectItem[] items = null;
		
		try {
			batchRetrySO = (ISOABatchRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SOABATCH,ISOABatchRetrySO.class);
			List<String> targetSystemList =  batchRetrySO.getAllTargetSystems();
		    
			if(targetSystemList != null){
			
			    items = new SelectItem[targetSystemList.size()];
			      
				for(int i = 0; i<targetSystemList.size(); i++) {
					String targetSystem = targetSystemList.get(i);
					items[i] = new SelectItem(targetSystem, targetSystem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));

		} finally {
			batchRetrySO = null;
		}
		
	    return items;
	}
	
	public String getRetryCountFieldColor(Integer retryCount){
		return SOAUtil.getRetryCountFieldColor(retryCount);
	}
}
