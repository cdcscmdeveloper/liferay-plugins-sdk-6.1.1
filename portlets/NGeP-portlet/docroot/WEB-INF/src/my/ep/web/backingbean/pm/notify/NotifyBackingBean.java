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

	private IPagingDataModel<Object[]> notifyDataModel = new NotifyDataModel(); // #27009

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
	
	// #27009 -- start
	public void selectedNotifys() {
		
		System.out.println("=== selectedNotifys ===");
		
		Object[] selectedNotifyObj = this.getManagedBean().getSelectedNotifyObj();
		
		Long notifyUserId = new Long(selectedNotifyObj[0].toString());
		boolean readSelectedNotifyObj = Boolean.parseBoolean(String.valueOf(selectedNotifyObj[12]));
		
		System.out.println("notifyUserId= "+notifyUserId);
		
		PmNotifyUser selectedNotify = new PmNotifyUser();
		
		INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", INotifySO.class);
		
		if (this.getManagedBean().getSelectedNotify() == null) {
			// Retrieve notifyUser
			selectedNotify = reqSO.getPmNotifyUserByNotifyUserId(notifyUserId);
		}
		
		if (!readSelectedNotifyObj) {
		
			NotifyReqDTO reqDTO = new NotifyReqDTO();
			reqDTO.setPmNotifyUser(selectedNotify);
			
			NotifyResDTO resDTO = reqSO.updateIsReadNotify(reqDTO);
			System.out.println("NotifyResDTO updateIsReadNotify["
					+ resDTO.isSuccessful() + "]");
			
			if (resDTO.isSuccessful()) {
				selectedNotify.setRead(true);
			}
		}
	}
	// #27009 -- end
	
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
	// #27009
	private class NotifyDataModel extends PagingDataModel<Object[]> {

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
		// #27009 -- start
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
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
					
					NotifyReqDTO reqDTO = new NotifyReqDTO();
					//reqDTO.setSearchBean(searchBean);
					reqDTO.setNativeSearchBean(searchBean);
					reqDTO.setUserId(user.getUserId());
					
					NotifyResDTO resDTO = ntfSO.searchNativeNotifyList(reqDTO);
					
					if (resDTO.getNativeSearchResultBeanObj() != null) {
						if (!resDTO.getNativeSearchResultBeanObj().getResultList().isEmpty() &&
								resDTO.getNativeSearchResultBeanObj().getResultList() != null) {
							System.out.println("getNativeSearchResultBeanObj= " +
									resDTO.getNativeSearchResultBeanObj().getResultList().size());
							/* For debugging..
							 * for (int i = 0; i < resDTO.getNativeSearchResultBeanObj().getResultList().size(); i++) {
								Object[] objects = (Object[]) resDTO.getNativeSearchList().get(i);
								System.out.println("notify obj= " + String.valueOf(objects[27]) 
										+", "+ String.valueOf(objects[5])+", "+String.valueOf(objects[28]));
							}*/

						} else {
							System.out.println(" getNativeSearchResultBeanObj size 0 "); 
						}
					}
					
					if (resDTO.isSuccessful()) {
						for (int i = 0; i < resDTO.getNativeSearchResultBeanObj().getResultList().size(); i++) {
							Object[] objects = (Object[]) resDTO.getNativeSearchList().get(i);
							getManagedBean().setSelectedNotifyObj(objects);
						}
						return resDTO.getNativeSearchResultBeanObj();
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
	// #27009 -- end

	/**
	 * @return the specMasterDataModel
	 */
	public IPagingDataModel<Object[]> getNotifyDataModel() {
		return notifyDataModel;
	}

	/**
	 * @param specMasterDataModel
	 *            the specMasterDataModel to set
	 */
	public void setNotifyDataModel(
			IPagingDataModel<Object[]> specMasterDataModel) {
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
