package my.ep.web.backingbean.soa.retrylog;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.soa.retrylog.ISOARetryLogSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.soa.retrylog.SOARetryLogReqDTO;
import my.ep.dto.soa.retrylog.SOARetryLogResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbRetryDtl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.soa.retrylog.SOARetryLogManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "soaRetryLogBackingBean")
@ViewScoped
public class SOARetryLogBackingBean extends
		BaseBackingBean<SOARetryLogManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(SOARetryLogBackingBean.class);

	private IPagingDataModel<OsbRetryDtl> retryLogDataModel = new SOARetryLogDataModel();

	public SOARetryLogBackingBean() {
	}

	/**
	 * template spec list paging class
	 * 
	 * @author MD
	 */
	private class SOARetryLogDataModel extends PagingDataModel<OsbRetryDtl> {

		private static final long serialVersionUID = 1L;

		public SOARetryLogDataModel() {
			super("retryLogTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<OsbRetryDtl> pageChangeListener(
				ISearchBean<OsbRetryDtl> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			ISOARetryLogSO logSO;
			try {
				logSO = (ISOARetryLogSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SOA,
								ISOARetryLogSO.class);

				SOARetryLogReqDTO reqDTO = new SOARetryLogReqDTO();
				SOARetryLogResDTO resDTO = logSO.searchSOARetryLogList(reqDTO);
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

	public void refreshRetryLogList() {
		debugLogger("refreshRetryLogList");
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Records refreshed.", ""));
		resetAndReloadData();
	}

	public void submitRetryLogList() {
		submitOrRemoveRetryLogList(true);
	}

	public void removeRetryLogList() {
		submitOrRemoveRetryLogList(false);
	}
	
	/**
	 * Private methods.
	 */

	private void submitOrRemoveRetryLogList(boolean isSubmit) {

		if (isSubmit) {
			debugLogger("submitRetryLogList");
		} else {
			debugLogger("removeRetryLogList");
		}

		SOARetryLogReqDTO reqDTO = new SOARetryLogReqDTO();

		for (OsbRetryDtl retryDtl : this.getManagedBean()
				.getSelectedRetryLogs()) {
			debugLogger("RetryDtlId : " + retryDtl.getRetryDtlId());
			reqDTO.getOsbRetryDtl().add(retryDtl);
		}

		if (reqDTO.getOsbRetryDtl().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please select at least a record. ", ""));
			
			return;
		}
		
		ISOARetryLogSO logSO = (ISOARetryLogSO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.SOA,
						ISOARetryLogSO.class);

		SOARetryLogResDTO resDTO = null;

		try{
		
			if (isSubmit) {
				resDTO = logSO.submitSOARetryLogList(reqDTO);
			} else {
				resDTO = logSO.removeSOARetryLogList(reqDTO);
			}

			debugLogger("NotifyResDTO : at least one record isSuccessful[" + resDTO.isSuccessful()+ "]");

			if (resDTO.isSuccessful()) {

				String message = null;

				if (resDTO.isAllRemoved()) {
					message = "Record(s) removed successfully.";
				} else {
					message = "Some record(s) removed successfully.";
				}

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, message,
								""));
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Unable to remove any record due to " + resDTO.getErrorMessage(), ""));
			}
		} catch (Exception ex){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to remove any record due to " + ex.getMessage(),""));
		}

		resetAndReloadData();
	}

	private void resetAndReloadData(){
		retryLogDataModel.resetData();
		retryLogDataModel.reloadData();
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

	public IPagingDataModel<OsbRetryDtl> getRetryLogDataModel() {
		return retryLogDataModel;
	}

	public void setRetryLogDataModel(
			IPagingDataModel<OsbRetryDtl> retryLogDataModel) {
		this.retryLogDataModel = retryLogDataModel;
	}
}
