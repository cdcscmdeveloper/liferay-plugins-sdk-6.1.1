package my.ep.web.backingbean.pm.notify;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.notify.INotifySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.notify.NotifyReqDTO;
import my.ep.dto.pm.common.notify.NotifyResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.notify.NotifyManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "notifyBackingBean")
@ViewScoped
public class NotifyBackingBean extends BaseBackingBean<NotifyManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(NotifyBackingBean.class);

	private IPagingDataModel<PmNotifyUser> notifyDataModel = new NotifyDataModel();

	public NotifyBackingBean() {
	}

	public void retrieveNotifyList() {
		debugLogger("retrieveNotifyList");

		this.getManagedBean().setUnRead(false);

		notifyDataModel.resetData();
		notifyDataModel.reloadData();
	}

	public void retrieveUnreadNotifyList() {
		debugLogger("retrieveUnreadNotifyList");

		this.getManagedBean().setUnRead(true);
		notifyDataModel.reloadData();
	}

	public void removeNotifys() {
		debugLogger("removeNotifys");
		NotifyReqDTO reqDTO = new NotifyReqDTO();
		if (this.getManagedBean().getSelectedNotifys() != null) {
			debugLogger("getSelectedNotifys length["
					+ this.getManagedBean().getSelectedNotifys().length + "]");
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);

			for (PmNotifyUser notifyUser : this.getManagedBean()
					.getSelectedNotifys()) {
				reqDTO.getPmNotifyUserList().add(notifyUser);
			}
			NotifyResDTO resDTO = reqSO.removeNotifyList(reqDTO);
			debugLogger("NotifyResDTO isSuccessful[" + resDTO.isSuccessful()
					+ "]");
			if (resDTO.isSuccessful()) {
				resetSelectedNotify();
				String message = "";
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record removed successfully.", message));
				notifyDataModel.reloadData();
			}
		}
	}

	public void selectedNotify() {
		debugLogger("=== selectedNotify ===");
		PmNotifyUser selectedNotify = this.getManagedBean().getSelectedNotify();
		debugLogger("IsRead[" + selectedNotify.getPmAlert().getIsRead() + "]");
		if (selectedNotify.getPmAlert().getIsRead() == 0) {
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			NotifyReqDTO reqDTO = new NotifyReqDTO();
			reqDTO.setPmNotifyUser(selectedNotify);
			NotifyResDTO resDTO = reqSO.updateIsReadNotify(reqDTO);
			debugLogger("NotifyResDTO updateIsReadNotify["
					+ resDTO.isSuccessful() + "]");
			if (resDTO.isSuccessful()) {
				selectedNotify.getPmAlert().setIsRead(1);
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

					// Filter By - Notification Mode
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean("notificationMode",
									SearchOperator.OPERATOR_EQUALS.value(), "A"));

					// Filter By - recipient Id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUser.userId",
									SearchOperator.OPERATOR_EQUALS.value(),
									user.getUserId()));

					// Filter By - status
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmAlert.recordStatus",
									SearchOperator.OPERATOR_EQUALS.value(), 1));

					// Order by address Id.
					searchBean.getSortColumns().add(
							new SearchOrderBean("pmNotifyLog.createdDate",
									false));

					if (getManagedBean().isUnRead()) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmAlert.isRead",
										SearchOperator.OPERATOR_EQUALS.value(),
										0));
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

	private void resetSelectedNotify() {
		getManagedBean().setSelectedNotify(null);
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
}
