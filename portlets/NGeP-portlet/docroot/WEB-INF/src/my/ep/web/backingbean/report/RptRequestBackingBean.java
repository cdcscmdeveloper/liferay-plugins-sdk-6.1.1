package my.ep.web.backingbean.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.notify.INotifySO;
import my.ep.biz.service.pm.common.report.IRPTRequest;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.notify.NotifyReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.persistence.entity.rpt.RPTRequest;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.report.RPTRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "rptRequestBackingBean")
@ViewScoped
public class RptRequestBackingBean extends BaseBackingBean<RPTRequestManagedBean> {
	
	private static final Logger logger = EpLogger
			.getLogger(RptRequestBackingBean.class);

	private IPagingDataModel<RPTRequest> rptRequestDataModel = new ReportDataModel();

	public RptRequestBackingBean() {
		this.getManagedBean().setDeleteButtonHide(true);
		this.getManagedBean().setSearchItem("1");
		this.getManagedBean().setSearchValue("");
	}

	public void retrieveRptRequestList() {
		
		debugLogger("retrieveRptRequestList");

		this.getManagedBean().setUnRead(false);
		this.getManagedBean().setSearchItem("1");
		this.getManagedBean().setSearchValue("");
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
		
		resetSelectedNotify();
	}

	public void removeRptRequests() {
		
		debugLogger("removeReports");
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			debugLogger("getSelectedNotifys length["
					+ this.getManagedBean().getSelectedRPTRequests().length + "]");
			
			IRPTRequest reqSO = (IRPTRequest) BusinessDelegate.getInstance()
					.getServiceObject("rpt", IRPTRequest.class);
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			//reload the same object from db
			
			long[] pmNotifyUserIds = new long[rptRequestArr.length];
			
			for(int i = 0; i<rptRequestArr.length; i++){
				//pmNotifyUserIds[i] = rptRequestArr[i].getNotifyUserId();
			}

			//rptRequestArr = reqSO.getPmReportList(pmNotifyUserIds);

			//end
			
			if(rptRequestArr.length > 0){
				
				this.getManagedBean().setSelectedRPTRequests(rptRequestArr);
				
				for (RPTRequest rptRequest : this.getManagedBean()
						.getSelectedRPTRequests()) {
					reqDTO.getRPTRequestList().add(rptRequest);
				}
				
				RPTRequestResDTO resDTO = reqSO.removeReportList(reqDTO);
				
				debugLogger("NotifyResDTO isSuccessful[" + resDTO.isSuccessful()
						+ "]");
				
				if (resDTO.isSuccessful()) {
					resetSelectedNotify();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									ResourceBundleUtils.getMessage("lbl.portal.notify.recordRemovedSuccesssfully",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
					rptRequestDataModel.reloadData();
				}
				
			} else {
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceBundleUtils.getMessage("lbl.portal.notify.selectAtLeastARecord",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
				rptRequestDataModel.reloadData();
			}
		}
		
		resetSelectedNotify();
	}

	public void selectedNotify() {
		
		debugLogger("=== selectedNotify ===");
		
		RPTRequest rptRequest = this.getManagedBean().getSelectedRPTRequest();
		
	}
	
	public void rowSelectUnselectCheckbox(){
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			if(rptRequestArr != null && rptRequestArr.length > 0){

				this.getManagedBean().setDeleteButtonHide(false);
			
			} else {
			
				this.getManagedBean().setDeleteButtonHide(true);
			}
			
		}
	}

	/**
	 * @author Jimmy
	 */

	private class ReportDataModel extends PagingDataModel<RPTRequest> {

		public ReportDataModel() {
			super("reportTable");
		}

		@Override
		public ISearchResultBean<RPTRequest> pageChangeListener(
				ISearchBean<RPTRequest> searchBean) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("reportTable's pageChangeListener");
			}

			IRPTRequest reportSO;
			
			try {
			
				FacesScopes facesScopes = new FacesScopes();
				PmUser user = facesScopes.getUserSessionBean();

				if (user == null) {
					logger.error("User is empty.");
				} else {
					reportSO = (IRPTRequest) BusinessDelegate.getInstance()
							.getServiceObject("rpt",
									IRPTRequest.class);

					// Filter By - recipient Id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("userId",
									SearchOperator.OPERATOR_EQUALS.value(),
									user.getUserId()));

					// Order by created date.
					searchBean.getSortColumns().add(
							new SearchOrderBean("createdDate",
									false));
						
					RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
					reqDTO.setSearchBean(searchBean);
					RPTRequestResDTO resDTO = reportSO.searchReportList(reqDTO);
					
					if (resDTO.isSuccessful()) {
						return resDTO.getSearchResultBean();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reportSO = null;
			}

			return null;
		}
	}

	/**
	 * @return the specMasterDataModel
	 */
	public IPagingDataModel<RPTRequest> getrptRequestDataModel() {
		return rptRequestDataModel;
	}

	/**
	 * @param specMasterDataModel
	 *            the specMasterDataModel to set
	 */
	public void setrptRequestDataModel(
			IPagingDataModel<RPTRequest> specMasterDataModel) {
		this.rptRequestDataModel = specMasterDataModel;
	}
	
	public void searchNotify() {
		
		//this.getManagedBean().setSelectedNotify(new PmNotifyUser());
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
		resetSelectedNotify();
	}
	
	public void searchItem(AjaxBehaviorEvent event) {
		
		String itemId = (String) event.getComponent().getAttributes().get("value");
		
		if(itemId == null){
			return;
		}
		
		if(itemId.equals("1")){
			this.getManagedBean().setSearchValue("");
		}
	}
	
	public void searchValue(AjaxBehaviorEvent event) {
		
		String itemId = (String) event.getComponent().getAttributes().get("value");
		
		if(itemId == null){
			return;
		}
	}
	
	private void resetSelectedNotify() {
		getManagedBean().setSelectedRPTRequests(null);
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
}
