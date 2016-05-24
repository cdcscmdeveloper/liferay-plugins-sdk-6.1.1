package my.ep.web.backingbean.notify.retry;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.ntf.retry.INtfRetrySO;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;

import my.ep.dto.ntf.retry.NotifyfRetryReqDTO;
import my.ep.dto.ntf.retry.NotifyRetryResDTO;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmNotifyMessage;

import my.ep.util.MessageUtils;
import my.ep.util.SOAUtil;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.notify.retry.NotifyRetryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "notifyRetryBackingBean")
@ViewScoped
public class NotifyRetryBackingBean extends
		BaseBackingBean<NotifyRetryManagedBean> {
	
	private static final Logger logger = EpLogger
			.getLogger(NotifyRetryBackingBean.class);

	private IPagingDataModel<PmNotifyMessage> notifyRetryDataModel = new NotifyRetryDataModel();

	public NotifyRetryBackingBean() {
	}
	
	/**
	 * @author Jimmy
	 */
	
	private class NotifyRetryDataModel extends PagingDataModel<PmNotifyMessage> {

		private static final long serialVersionUID = 1L;

		public NotifyRetryDataModel() {
			super("notifyRetryTable");
		}

		@Override
		public ISearchResultBean<PmNotifyMessage> pageChangeListener(
				ISearchBean<PmNotifyMessage> iSearchBean) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			INtfRetrySO ntfRetrySO;			
			
			try {
			
				ntfRetrySO = (INtfRetrySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.NTF_SCH,
								INtfRetrySO.class);

				NotifyfRetryReqDTO reqDTO = new NotifyfRetryReqDTO();
				
				reqDTO.setISearchBean(iSearchBean);
				reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
				
				NotifyRetryResDTO resDTO = ntfRetrySO.searchNtfRetryList(reqDTO);
				
				if (resDTO.isSuccessful()) {
					return resDTO.getSearchResultBean();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ntfRetrySO = null;
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

		NotifyfRetryReqDTO reqDTO = new NotifyfRetryReqDTO();
		
		reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
		
		for (PmNotifyMessage retryDtl : this.getManagedBean().getSelectedNotifyRetries()){
			debugLogger("NotifyMessageId : " + retryDtl.getNotifyMessageId());
			reqDTO.getNtfMessageList().add(retryDtl);
		}

		if (reqDTO.getNtfMessageList().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							MessageUtils.getMessage(MessageConst.ERROR_990001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
							, null));
			
			return;
		}
		
		INtfRetrySO ntfRetrySO = (INtfRetrySO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.NTF_SCH,
						INtfRetrySO.class);

		NotifyRetryResDTO resDTO = null;

		try{
		
			if (isSubmit) {
				resDTO = ntfRetrySO.submitNtfRetryList(reqDTO);
			} else {
				resDTO = ntfRetrySO.removeNtfRetryList(reqDTO);
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

	public IPagingDataModel<PmNotifyMessage> getNotifyRetryDataModel() {
		return notifyRetryDataModel;
	}

	public void setNotifyRetryDataModel(
			IPagingDataModel<PmNotifyMessage> notifyRetryDataModel) {
		this.notifyRetryDataModel = notifyRetryDataModel;
	}
	
	public SelectItem[] getStatusDropdown() {
		
		INtfRetrySO ntfRetrySO = null;
		SelectItem[] items = null;
		
		try {
			ntfRetrySO = (INtfRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.NTF_SCH, INtfRetrySO.class);
			List<String> targetSystemList =  ntfRetrySO.getAllTargetSystems();
		    
		    items = new SelectItem[targetSystemList.size()];
		      
			for(int i = 0; i<targetSystemList.size(); i++) {
				String targetSystem = targetSystemList.get(i);
				items[i] = new SelectItem(targetSystem, targetSystem);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			ntfRetrySO = null;
		}
		
	    return items;
	}
	
	public String getRetryCountFieldColor(Integer retryCount){
		return SOAUtil.getRetryCountFieldColor(retryCount);
	}
}
