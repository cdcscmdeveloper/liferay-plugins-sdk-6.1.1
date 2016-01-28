package my.ep.web.backingbean.soa.retry;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.soa.retry.ISOANotifyRetrySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.soa.retry.SOANotifyRetryReqDTO;
import my.ep.dto.soa.retry.SOANotifyRetryResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbNotifyRetryDtl;
import my.ep.util.MessageUtils;
import my.ep.util.SOAUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.soa.retry.SOANotifyRetryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


@ManagedBean(name = "soaNotifyRetryBackingBean")
@ViewScoped
public class SOANotifyRetryBackingBean extends
		BaseBackingBean<SOANotifyRetryManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(SOANotifyRetryBackingBean.class);

	private IPagingDataModel<OsbNotifyRetryDtl> notifyRetryDataModel = new SOANotifyRetryDataModel();

	public SOANotifyRetryBackingBean() {
	}

	/**
	 * 
	 * @author Jimmy
	 */
	private class SOANotifyRetryDataModel extends PagingDataModel<OsbNotifyRetryDtl> {

		private static final long serialVersionUID = 1L;

		public SOANotifyRetryDataModel() {
			super("notifyRetryTable");
		}

		@Override
		public ISearchResultBean<OsbNotifyRetryDtl> pageChangeListener(
				ISearchBean<OsbNotifyRetryDtl> iSearchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			ISOANotifyRetrySO logSO;			try {
				logSO = (ISOANotifyRetrySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SOANTF,
								ISOANotifyRetrySO.class);

				SOANotifyRetryReqDTO reqDTO = new SOANotifyRetryReqDTO();
				
				reqDTO.setISearchBean(iSearchBean);
				reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
				
				SOANotifyRetryResDTO resDTO = logSO.searchSOANotifyRetryList(reqDTO);
				
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

	public void refreshNotifyRetryList() {
		debugLogger("refreshNotifyRetryList");
		resetAndReloadData();
	}

	public void submitNotifyRetryList() {
		submitOrRemoveNotifyRetryList(true);
	}

	public void removeNotifyRetryList() {
		submitOrRemoveNotifyRetryList(false);
	}
	
	/**
	 * Private methods.
	 */

	private void submitOrRemoveNotifyRetryList(boolean isSubmit) {

		if (isSubmit) {
			debugLogger("submitNotifyRetryList");
		} else {
			debugLogger("removeNotifyRetryList");
		}

		SOANotifyRetryReqDTO reqDTO = new SOANotifyRetryReqDTO();
		
		reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
		
		for (OsbNotifyRetryDtl retryDtl : this.getManagedBean().getSelectedNotifyRetries()){
			debugLogger("RetryDtlId : " + retryDtl.getRetryDtlId());
			reqDTO.getOsbNotifyRetryDtl().add(retryDtl);
		}

		if (reqDTO.getOsbNotifyRetryDtl().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							MessageUtils.getMessage(MessageConst.ERROR_990001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
							, null));
			
			return;
		}
		
		ISOANotifyRetrySO logSO = (ISOANotifyRetrySO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.SOANTF,
						ISOANotifyRetrySO.class);

		SOANotifyRetryResDTO resDTO = null;

		try{
		
			if (isSubmit) {
				resDTO = logSO.submitSOANotifyRetryList(reqDTO);
			} else {
				resDTO = logSO.removeSOANotifyRetryList(reqDTO);
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
		notifyRetryDataModel.resetData();
		notifyRetryDataModel.reloadData();
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

	public IPagingDataModel<OsbNotifyRetryDtl> getNotifyRetryDataModel() {
		return notifyRetryDataModel;
	}

	public void setNotifyRetryDataModel(
			IPagingDataModel<OsbNotifyRetryDtl> notifyRetryDataModel) {
		this.notifyRetryDataModel = notifyRetryDataModel;
	}
	
	public SelectItem[] getStatusDropdown() {
		
		ISOANotifyRetrySO notifyRetrySO = null;
		SelectItem[] items = null;
		
		try {
			notifyRetrySO = (ISOANotifyRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SOANTF,ISOANotifyRetrySO.class);
			List<String> targetSystemList =  notifyRetrySO.getAllTargetSystems();
		    
		    items = new SelectItem[targetSystemList.size()];
		      
			for(int i = 0; i<targetSystemList.size(); i++) {
				String targetSystem = targetSystemList.get(i);
				items[i] = new SelectItem(targetSystem, targetSystem);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			notifyRetrySO = null;
		}
		
	    return items;
	}
	
	public String getRetryCountFieldColor(Integer retryCount){
		return SOAUtil.getRetryCountFieldColor(retryCount);
	}
}
