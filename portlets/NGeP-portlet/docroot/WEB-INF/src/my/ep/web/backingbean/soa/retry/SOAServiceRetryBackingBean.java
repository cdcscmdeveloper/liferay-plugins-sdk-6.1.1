package my.ep.web.backingbean.soa.retry;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.soa.retry.ISOAServiceRetrySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.soa.retry.SOAServiceRetryReqDTO;
import my.ep.dto.soa.retry.SOAServiceRetryResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbRetryDtl;
import my.ep.util.MessageUtils;
import my.ep.util.SOAUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.soa.retry.SOAServiceRetryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


@ManagedBean(name = "soaServiceRetryBackingBean")
@ViewScoped
public class SOAServiceRetryBackingBean extends
		BaseBackingBean<SOAServiceRetryManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(SOAServiceRetryBackingBean.class);

	private IPagingDataModel<OsbRetryDtl> serviceRetryDataModel = new SOAServiceRetryDataModel();

	public SOAServiceRetryBackingBean() {
	}

	/**
	 * template spec list paging class
	 * 
	 * @author MD
	 */
	private class SOAServiceRetryDataModel extends PagingDataModel<OsbRetryDtl> {

		private static final long serialVersionUID = 1L;

		public SOAServiceRetryDataModel() {
			super("serviceRetryTable");
		}

		@Override
		public ISearchResultBean<OsbRetryDtl> pageChangeListener(
				ISearchBean<OsbRetryDtl> iSearchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			ISOAServiceRetrySO logSO;			try {
				logSO = (ISOAServiceRetrySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SOA,
								ISOAServiceRetrySO.class);

				SOAServiceRetryReqDTO reqDTO = new SOAServiceRetryReqDTO();
				
				reqDTO.setISearchBean(iSearchBean);
				reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
				
				SOAServiceRetryResDTO resDTO = logSO.searchSOAServiceRetryList(reqDTO);
				
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

	public void refreshServiceRetryList() {
		debugLogger("refreshServiceRetryList");
//		FacesContext.getCurrentInstance().addMessage(
//				null,
//				new FacesMessage(FacesMessage.SEVERITY_INFO,
//						"Records refreshed.", ""));
		resetAndReloadData();
	}

	public void submitServiceRetryList() {
		submitOrRemoveServiceRetryList(true);
	}

	public void removeServiceRetryList() {
		submitOrRemoveServiceRetryList(false);
	}
	
	/**
	 * Private methods.
	 */

	private void submitOrRemoveServiceRetryList(boolean isSubmit) {

		if (isSubmit) {
			debugLogger("submitServiceRetryList");
		} else {
			debugLogger("removeServiceRetryList");
		}

		SOAServiceRetryReqDTO reqDTO = new SOAServiceRetryReqDTO();
		
		reqDTO.setTargetSystem(getManagedBean().getTargetSystem());
		
		for (OsbRetryDtl retryDtl : this.getManagedBean().getSelectedServiceRetries()){
			debugLogger("RetryDtlId : " + retryDtl.getRetryDtlId());
			reqDTO.getOsbRetryDtl().add(retryDtl);
		}

		if (reqDTO.getOsbRetryDtl().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							MessageUtils.getMessage(MessageConst.ERROR_990001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
							, null));
			
			return;
		}
		
		ISOAServiceRetrySO logSO = (ISOAServiceRetrySO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.SOA,
						ISOAServiceRetrySO.class);

		SOAServiceRetryResDTO resDTO = null;

		try{
		
			if (isSubmit) {
				resDTO = logSO.submitSOAServiceRetryList(reqDTO);
			} else {
				resDTO = logSO.removeSOAServiceRetryList(reqDTO);
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
		serviceRetryDataModel.resetData();
		serviceRetryDataModel.reloadData();
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

	public IPagingDataModel<OsbRetryDtl> getServiceRetryDataModel() {
		return serviceRetryDataModel;
	}

	public void setServiceRetryDataModel(
			IPagingDataModel<OsbRetryDtl> serviceRetryDataModel) {
		this.serviceRetryDataModel = serviceRetryDataModel;
	}
	
	public SelectItem[] getStatusDropdown() {
		
		ISOAServiceRetrySO serviceRetrySO = null;
		SelectItem[] items = null;
		
		try {
			serviceRetrySO = (ISOAServiceRetrySO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SOA,ISOAServiceRetrySO.class);
			List<String> targetSystemList =  serviceRetrySO.getAllTargetSystems();
		    
		    items = new SelectItem[targetSystemList.size()];
		      
			for(int i = 0; i<targetSystemList.size(); i++) {
				String targetSystem = targetSystemList.get(i);
				items[i] = new SelectItem(targetSystem, targetSystem);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));

		} finally {
			serviceRetrySO = null;
		}
		
	    return items;
	}
	
	public String getRetryCountFieldColor(Integer retryCount){
		return SOAUtil.getRetryCountFieldColor(retryCount);
	}
}
