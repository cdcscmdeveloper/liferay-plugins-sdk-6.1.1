package my.ep.web.backingbean.pm.notify;

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
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.notify.INotifySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.dto.pm.common.notify.NotifyReqDTO;
import my.ep.dto.pm.common.notify.NotifyResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.notify.NotifyManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "notifyBackingBean")
@ViewScoped
public class NotifyBackingBean extends BaseBackingBean<NotifyManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(NotifyBackingBean.class);

	private IPagingDataModel<PmNotifyUser> notifyDataModel = new NotifyDataModel();

	public NotifyBackingBean() {
		this.getManagedBean().setDeleteButtonHide(true);
		this.getManagedBean().setSearchItem("1");
		this.getManagedBean().setSearchValue("");
	}

	public void retrieveNotifyList() {
		
		
		debugLogger("retrieveNotifyList");

		this.getManagedBean().setUnRead(false);
		this.getManagedBean().setSearchItem("1");
		this.getManagedBean().setSearchValue("");
		
		notifyDataModel.resetData();
		notifyDataModel.reloadData();
		resetSelectedNotify();
	}

	public void retrieveUnreadNotifyList() {
		debugLogger("retrieveUnreadNotifyList");

		this.getManagedBean().setUnRead(true);
		notifyDataModel.reloadData();
		resetSelectedNotify();
	}

	public void removeNotifys() {
		
		debugLogger("removeNotifys");
		NotifyReqDTO reqDTO = new NotifyReqDTO();
		
		if (this.getManagedBean().getSelectedNotifys() != null) {
			
			debugLogger("getSelectedNotifys length["
					+ this.getManagedBean().getSelectedNotifys().length + "]");
			
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			
			PmNotifyUser[] pmNotifyUserArr = this.getManagedBean().getSelectedNotifys();
			
			//reload the same object from db
			
			long[] pmNotifyUserIds = new long[pmNotifyUserArr.length];
			
			for(int i = 0; i<pmNotifyUserArr.length; i++){
				pmNotifyUserIds[i] = pmNotifyUserArr[i].getNotifyUserId();
			}

			pmNotifyUserArr = reqSO.getPmNotifyUser(pmNotifyUserIds);

			//end
			
			if(pmNotifyUserArr.length > 0){
				
				this.getManagedBean().setSelectedNotifys(pmNotifyUserArr);
				
				for (PmNotifyUser notifyUser : this.getManagedBean()
						.getSelectedNotifys()) {
					reqDTO.getPmNotifyUserList().add(notifyUser);
				}
				
				NotifyResDTO resDTO = reqSO.removeNotifyList(reqDTO);
				
				debugLogger("NotifyResDTO isSuccessful[" + resDTO.isSuccessful()
						+ "]");
				
				if (resDTO.isSuccessful()) {
					resetSelectedNotify();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									ResourceBundleUtils.getMessage("lbl.portal.notify.recordRemovedSuccesssfully",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
					notifyDataModel.reloadData();
				}
				
			} else {
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceBundleUtils.getMessage("lbl.portal.notify.selectAtLeastARecord",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
				notifyDataModel.reloadData();
			}
		}
		
		resetSelectedNotify();
	}

	public void selectedNotify() {
		
		debugLogger("=== selectedNotify ===");
		
		PmNotifyUser selectedNotify = this.getManagedBean().getSelectedNotify();
		
		debugLogger("IsRead[" + selectedNotify.isRead() + "]");
		
		if (!selectedNotify.isRead()) {
		
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			
			NotifyReqDTO reqDTO = new NotifyReqDTO();
			reqDTO.setPmNotifyUser(selectedNotify);
			
			NotifyResDTO resDTO = reqSO.updateIsReadNotify(reqDTO);
			debugLogger("NotifyResDTO updateIsReadNotify["
					+ resDTO.isSuccessful() + "]");
			
			if (resDTO.isSuccessful()) {
				selectedNotify.setRead(true);
			}
		}
	}
	
	public void rowSelectUnselectCheckbox(){
		
		if (this.getManagedBean().getSelectedNotifys() != null) {
			
			PmNotifyUser[] pmNotifyUserArr = this.getManagedBean().getSelectedNotifys();
			
			if(pmNotifyUserArr != null && pmNotifyUserArr.length > 0){

				this.getManagedBean().setDeleteButtonHide(false);
			
			} else {
			
				this.getManagedBean().setDeleteButtonHide(true);
			}
			
		}
	}

	/**
	 * template spec list paging class
	 * 
	 * @author MD
	 */
	private class NotifyDataModel extends PagingDataModel<PmNotifyUser> {

		private static final long serialVersionUID = 1L;

		public NotifyDataModel() {
			super("notifyTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmNotifyUser> pageChangeListener(
				ISearchBean<PmNotifyUser> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			INotifySO ntfSO;
			
			try {
				FacesScopes facesScopes = new FacesScopes();
				PmUser user = facesScopes.getUserSessionBean();

				if (user == null) {
					logger.error("User is empty.");
				} else {
					ntfSO = (INotifySO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									INotifySO.class);
					//20200601@asyrafhaziq_Enhancement #14291 - Start
					// Filter By - Notification Mode
					//searchBean.getDataFilterBeans().add(
					//		new SearchFilterBean("pmNotifyDashboard.notifyDashboardId",
					//				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null));
					
					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									new SearchFilterBean(
											"pmNotifyDashboard.notifyDashboardId",
											SearchOperator.OPERATOR_IS_NOT_NULL.value(),
											null), 
									new SearchFilterBean(
											"pmNotifyDashboardQt.notifyDashboardQtId",
											SearchOperator.OPERATOR_IS_NOT_NULL.value(),
											null,
									SearchCondition.CONDITION_OR.value())));
					//20200601@asyrafhaziq_Enhancement #14291 - End
					// Filter By - recipient Id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recipient.userId",
									SearchOperator.OPERATOR_EQUALS.value(),
									user.getUserId()));

					// Filter By - status
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatus",
									SearchOperator.OPERATOR_EQUALS.value(), 1));

					// Order by address Id.
					searchBean.getSortColumns().add(
							new SearchOrderBean("createdDate",
									false));

					if (getManagedBean().isUnRead()) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("isRead",
										SearchOperator.OPERATOR_EQUALS.value(),
										0));
					}
					
					if(getManagedBean().getSearchItem() != null && getManagedBean().getSearchItem().equals("2")){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("sender.userName",
										SearchOperator.OPERATOR_LIKE.value(), 
										getManagedBean().getSearchValue().trim(), true));
					}
					
					if(getManagedBean().getSearchItem() != null && getManagedBean().getSearchItem().equals("3")){
						// 20210104@fadhli - #29697 - Start
						SearchFilterBean pmNotifyDashboardFilterBean = new SearchFilterBean(
								"pmNotifyDashboard.subject", 
								SearchOperator.OPERATOR_LIKE.value(), 
								getManagedBean().getSearchValue().trim());
						pmNotifyDashboardFilterBean.setCaseInsensitive(true);
						
						SearchFilterBean pmNotifyDashboardQtFilterBean = new SearchFilterBean(
								"pmNotifyDashboardQt.subject", 
								SearchOperator.OPERATOR_LIKE.value(), 
								getManagedBean().getSearchValue().trim(), 
								SearchCondition.CONDITION_OR.value());
						pmNotifyDashboardQtFilterBean.setCaseInsensitive(true);
						
						searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
								pmNotifyDashboardFilterBean, pmNotifyDashboardQtFilterBean));
						// 20210104@fadhli - #29697 - End
					}
					
					
					if(getManagedBean().getSearchItem() != null && getManagedBean().getSearchItem().equals("4")){
						// 20210104@fadhli - #29697 - Start
						SearchFilterBean pmNotifyDashboardFilterBean = new SearchFilterBean(
								"pmNotifyDashboard.content", 
								SearchOperator.OPERATOR_LIKE.value(), 
								getManagedBean().getSearchValue().trim());
						pmNotifyDashboardFilterBean.setCaseInsensitive(true);
						
						SearchFilterBean pmNotifyDashboardQtFilterBean = new SearchFilterBean(
								"pmNotifyDashboardQt.content", 
								SearchOperator.OPERATOR_LIKE.value(), 
								getManagedBean().getSearchValue().trim(), 
								SearchCondition.CONDITION_OR.value());
						pmNotifyDashboardQtFilterBean.setCaseInsensitive(true);
						
						searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
								pmNotifyDashboardFilterBean, pmNotifyDashboardQtFilterBean));
						// 20210104@fadhli - #29697 - End
					}
						
					if(getManagedBean().getSearchItem() != null && getManagedBean().getSearchItem().equals("5")){
						
						Date date = covertStringToDate(getManagedBean().getSearchValue());
						
						if(date != null){
							//20200601@asyrafhaziq_Enhancement #14291 - Start
							//searchBean
							//		.getDataFilterBeans()
							//		.add(new SearchFilterBean("pmNotifyDashboard.createdDate",
							//			SearchOperator.OPERATOR_GREATER_EQUAL.value(), date));
							
							searchBean.getDataFilterBeans().add(
									new SearchMultiFilterBean(
											new SearchFilterBean(
													"pmNotifyDashboard.createdDate",
													SearchOperator.OPERATOR_GREATER_EQUAL.value(),
													date), 
											new SearchFilterBean(
													"pmNotifyDashboardQt.createdDate",
													SearchOperator.OPERATOR_GREATER_EQUAL.value(),
													date,
											SearchCondition.CONDITION_OR.value())));
							//20200601@asyrafhaziq_Enhancement #14291 - End
						}
					}
					NotifyReqDTO reqDTO = new NotifyReqDTO();
					reqDTO.setSearchBean(searchBean);
					NotifyResDTO resDTO = ntfSO.searchNotifyList(reqDTO);
					
					if (resDTO.isSuccessful()) {
						return resDTO.getSearchResultBean();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ntfSO = null;
			}

			return null;
		}
	}

	/**
	 * @return the specMasterDataModel
	 */
	public IPagingDataModel<PmNotifyUser> getNotifyDataModel() {
		return notifyDataModel;
	}

	/**
	 * @param specMasterDataModel
	 *            the specMasterDataModel to set
	 */
	public void setNotifyDataModel(
			IPagingDataModel<PmNotifyUser> specMasterDataModel) {
		this.notifyDataModel = specMasterDataModel;
	}
	
	public void searchNotify() {
		
		//this.getManagedBean().setSelectedNotify(new PmNotifyUser());
		
		notifyDataModel.resetData();
		notifyDataModel.reloadData();
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
	
	public String getMask(){
		
		if(getManagedBean().getSearchItem() != null && getManagedBean().getSearchItem().equals("5")){
			return "99-99-9999";
		} else {
			return "";
		}
	}
	
	private Date covertStringToDate(String dateInString){
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		
		try {
			date = formatter.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	private void resetSelectedNotify() {
		getManagedBean().setSelectedNotify(null);
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		//logger.info(message);
	}
	
	// #14291 - Start
	/**
	 * @param pmNotifyUser
	 * @return
	 */
	public String getNotifyDashboardContent(PmNotifyUser pmNotifyUser) {
		logger.debug("Entered: " + this.getClass().getName() + ".getNotifyDashboardContent()");
		
		if (null != pmNotifyUser) {
			String notificationId = String.valueOf(pmNotifyUser.getPmNotification().getNotificationId());
			logger.info(this.getClass().getName() + ".getNotifyDashboardContent(): notificationId is: " + notificationId);
			if (NotificationTextConst.TEMPLATE_ID_400048.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_401048.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_400050.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_401050.equals(notificationId)){
			
				if( null != pmNotifyUser.getPmNotifyDashboardQt()
						&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboardQt().getContent())){
					return pmNotifyUser.getPmNotifyDashboardQt().getContent();
				}
				else if( null != pmNotifyUser.getPmNotifyDashboard()
							&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboard().getContent())){
					return pmNotifyUser.getPmNotifyDashboard().getContent();
				}
		  }else {
			  
			  if( null != pmNotifyUser.getPmNotifyDashboard()
					&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboard().getContent())){
				  return pmNotifyUser.getPmNotifyDashboard().getContent();
				}
			}
		}
		return "";
	}
	
	public String getNotifyDashboardSubject(PmNotifyUser pmNotifyUser) {
		logger.debug("Entered: " + this.getClass().getName() + ".getNotifyDashboardContent()");
		
		if (null != pmNotifyUser) {
			String notificationId = String.valueOf(pmNotifyUser.getPmNotification().getNotificationId());
			logger.info(this.getClass().getName() + ".getNotifyDashboardContent(): notificationId is: " + notificationId);
			if (NotificationTextConst.TEMPLATE_ID_400048.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_401048.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_400050.equals(notificationId)
					|| NotificationTextConst.TEMPLATE_ID_401050.equals(notificationId)){
			
				if( null != pmNotifyUser.getPmNotifyDashboardQt()
						&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboardQt().getSubject())){
					return pmNotifyUser.getPmNotifyDashboardQt().getSubject();
				}
				else if( null != pmNotifyUser.getPmNotifyDashboard()
							&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboard().getSubject())){
					return pmNotifyUser.getPmNotifyDashboard().getSubject();
				}
		  }else {
			  
			  if( null != pmNotifyUser.getPmNotifyDashboard()
					&& StringUtils.isNotEmpty(pmNotifyUser.getPmNotifyDashboard().getSubject())){
				  return pmNotifyUser.getPmNotifyDashboard().getSubject();
				}
			}
		}
		return "";
	}
	// #14291 - End
	
}
